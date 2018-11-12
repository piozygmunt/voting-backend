package voting.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voting.exception.AppException;
import voting.exception.ResourceNotFoundException;
import voting.model.auth.User;
import voting.model.chat.MessageDTO;
import voting.model.common.ApiResponse;
import voting.model.common.Notification;
import voting.model.vote.*;
import voting.repositories.InvitationRepository;
import voting.repositories.PickRepository;
import voting.repositories.VotingInvitationRepository;
import voting.repositories.VotingProcessRepoitory;
import voting.service.utils.MsgDateComparator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VotingService {
    private final VotingProcessRepoitory votingProcessRepoitory;
    private final PickRepository pickRepository;
    private final InvitationRepository invitationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationService notificationService;
    private final VotingInvitationRepository votingInvitationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public VotingService(VotingProcessRepoitory votingProcessRepoitory, PickRepository pickRepository, InvitationRepository invitationRepository, SimpMessagingTemplate simpMessagingTemplate, NotificationService notificationService, VotingInvitationRepository votingInvitationRepository, ModelMapper modelMapper) {
        this.votingProcessRepoitory = votingProcessRepoitory;
        this.pickRepository = pickRepository;
        this.invitationRepository = invitationRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationService = notificationService;
        this.votingInvitationRepository = votingInvitationRepository;
        this.modelMapper = modelMapper;
    }

    private static List<Action> parseActionString(String actionString) {
        String initalParsedString = actionString.trim();

        List<Action> actions = new ArrayList<>();

        for (int i = 0; i < initalParsedString.length(); ++i) {
            if (initalParsedString.charAt(i) == 'D') actions.add(Action.DROP);
            else if (initalParsedString.charAt(i) == 'P') actions.add(Action.PICK);
            else return null;
        }
        return actions;
    }


    public VotingProcessDTO createVotingProcess(VotingProcessRequest votingProcessRequest, User user) {
        //validate
        log.info("voting req: {}", votingProcessRequest);

        VotingProcess votingProcess = new VotingProcess();
        votingProcess.setActionString(votingProcessRequest.getActionString());
        votingProcess.setPossibilities(votingProcessRequest.getItems());
        votingProcess.setCreatedBy(user);
        List<User> users = new ArrayList<>();

        User invitedUser = modelMapper.map(votingProcessRequest.getUser(), User.class);

        users.add(user);
        votingProcess.setUsers(users);

        log.info("user  {}", invitedUser);

        VotingInvitation invitation = new VotingInvitation();
        invitation.setInvitedUser(invitedUser);
        invitation.setVotingProcess(votingProcess);
        invitation.setDate(Instant.now());


        votingProcessRepoitory.save(votingProcess);

        notificationService.sendInviteNotification(Notification.Type.INVITE, "You have been invited to voting process", invitedUser, invitation);


        log.info("invitation: {}", invitationRepository.findAll());

        log.info("ID: {}", votingProcess.getId());

        return modelMapper.map(votingProcess, VotingProcessDTO.class);
    }

    public List<VotingProcessDTO> getAllVotingProcesses() {
        return votingProcessRepoitory.findAll().parallelStream()
                .map(votingProc -> {
                    VotingProcessDTO votingProcessDTO = modelMapper.map(votingProc, VotingProcessDTO.class);
                    votingProcessDTO.setMessages(null);
                    return votingProcessDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<VotingProcessDTO> getAllVotingProcessesByStateIsNot(VotingProcess.State state) {
        return votingProcessRepoitory.findAllByStateIsNot(state).parallelStream()
                .map(votingProc -> {
                    VotingProcessDTO votingProcessDTO = modelMapper.map(votingProc, VotingProcessDTO.class);
                    votingProcessDTO.setMessages(null);
                    return votingProcessDTO;
                })
                .collect(Collectors.toList());
    }

    public VotingProcessDTO getVotingProcess(long procId) {
        VotingProcess votingProcess = votingProcessRepoitory.findByIdAndStateIsNot(procId, VotingProcess.State.WAITING)
                .orElseThrow(() -> new ResourceNotFoundException("process ", "id", procId));
        ModelMapper modelMapper = new ModelMapper();
        MsgDateComparator ascComp = new MsgDateComparator(true);
        VotingProcessDTO votingProcessDTO = modelMapper.map(votingProcess, VotingProcessDTO.class);
        votingProcessDTO.setMessages(votingProcess.getMessages().stream().limit(5).sorted(ascComp).map(message -> {
                    MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
                    log.info("Message id {} user {} ", procId, message.getUser());
                    messageDTO.setUsername(message.getUser().getUsername());
                    return messageDTO;
                }
        ).collect(Collectors.toList()));
        return votingProcessDTO;
    }

    public VoteResponse vote(long procId, long userId, long itemId) {

        VotingProcess votingProcess = votingProcessRepoitory.findById(procId)
                .orElseThrow(() -> new ResourceNotFoundException("Process", "id", procId));
        VoteResponse voteResponse = new VoteResponse();

        if (votingProcess.getState().equals(VotingProcess.State.FINISHED)) {
            voteResponse.setPickMade(null);
            voteResponse.setNewState(VotingProcess.State.FINISHED);
            voteResponse.setNextUser(null);
            voteResponse.setVotedProcId(votingProcess.getId());
            return voteResponse;
        }

        User user = votingProcess.getCurrentUser();

        if (user.getId() != userId) {
            throw new AppException("Wrong user voted.");
        }

        PickingItem pickingItem = votingProcess.getPossibilities().stream()
                .filter(pickingItem1 -> pickingItem1.getId() == itemId).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Picking item", "id", itemId));

        List<Action> actions = parseActionString(votingProcess.getActionString());


        Action currentAction = actions.get(votingProcess.getCurrentStep());

        Pick pick = new Pick();
        pick.setUser(user);
        pick.setPickingItem(pickingItem);
        pick.setOrderIndex(votingProcess.getCurrentStep());
        pick.setAction(currentAction);

        pickRepository.save(pick);
        votingProcess.getPicks().add(pick);
        votingProcess.incrementStep();

        if (votingProcess.getCurrentStep() == votingProcess.getActionString().length()) {
            votingProcess.setState(VotingProcess.State.FINISHED);
        }

        log.info("voting process: {}", votingProcess);

        votingProcessRepoitory.save(votingProcess);

        voteResponse.setPickMade(pick);
        voteResponse.setNewState(votingProcess.getState());
        voteResponse.setNextUser(votingProcess.getCurrentUser());
        voteResponse.setVotedProcId(votingProcess.getId());


        simpMessagingTemplate.convertAndSend("/topic/proc/" + procId, voteResponse);

        return voteResponse;

    }

    public ApiResponse acceptInvitation(User user, long votingId, boolean accept) {

        log.info("voting {} accept {}", votingId, accept);
        VotingProcess votingProcess = votingProcessRepoitory.findById(votingId).orElseThrow(() -> new AppException("Voting process with given id was not found."));

        VotingInvitation votingInvitation = invitationRepository.findByVotingProcess(votingProcess).orElseThrow(() -> new AppException("Invitiation for this process was not found."));

        if (votingInvitation.getState().equals(VotingInvitation.State.PENDING)) {
            log.info("pending");
            if (votingInvitation.getInvitedUser().equals(user)) {
                votingInvitation.setState(accept ? VotingInvitation.State.ACCEPTED : VotingInvitation.State.REJECTED);
                votingInvitationRepository.save(votingInvitation);
                if (accept) {
                    votingProcess.setState(VotingProcess.State.STARTED);
                    votingProcess.getUsers().add(user);
                    votingProcessRepoitory.save(votingProcess);
                    return new ApiResponse(true, "Invitation accepted.");
                }
                return new ApiResponse(true, "Invitation rejected.");
            } else {
                throw new AppException("Invited user doesnt match.");
            }
        } else
            throw new AppException("Invitation already accepted or rejected");
    }

    public List<VotingInvitationDTO> getAllInvitationLinkedWith(User user) {
        return votingInvitationRepository.findAllByInvitedUserOrVotingProcessCreatedBy(user, user)
                .stream().map(votingInvitation -> modelMapper.map(votingInvitation, VotingInvitationDTO.class))
                .collect(Collectors.toList());
    }

}
