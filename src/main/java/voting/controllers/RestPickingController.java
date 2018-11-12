package voting.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import voting.model.auth.User;
import voting.model.chat.MessageRequest;
import voting.model.common.ApiResponse;
import voting.model.vote.*;
import voting.service.MessageService;
import voting.service.VotingService;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin
public class RestPickingController {
    private final VotingService votingService;
    private final MessageService messageService;

    @Autowired
    public RestPickingController(VotingService votingService, MessageService messageService) {
        this.votingService = votingService;
        this.messageService = messageService;
    }

    @GetMapping("/api/proc")
    public List<VotingProcessDTO> getAllVotingProc() {
        log.info("Getting all voting proces.");
        return votingService.getAllVotingProcessesByStateIsNot(VotingProcess.State.WAITING);
    }

    @PostMapping("/api/proc")
    public VotingProcessDTO addVotingProcess(@RequestBody VotingProcessRequest votingProcessRequest, @AuthenticationPrincipal User user) {
        return votingService.createVotingProcess(votingProcessRequest, user);
    }

    @GetMapping("/api/proc/{id}")
    public VotingProcessDTO getProcApi(@PathVariable("id") long id) {
        log.info("getting data");
        return votingService.getVotingProcess(id);
    }



    @GetMapping("/api/invitations")
    public List<VotingInvitationDTO> getItemsStartingWith(@AuthenticationPrincipal User user) {
        return votingService.getAllInvitationLinkedWith(user);
    }


    @GetMapping("/api/proc/{procId}/vote/{itemId}")
    public VoteResponse vote(@PathVariable("procId") long procId, @PathVariable("itemId") long itemId, @AuthenticationPrincipal User user) {
        return votingService.vote(procId, user.getId(), itemId);
    }

    @GetMapping("/api/proc/{procId}/accept")
    public ApiResponse acceptInvite(@PathVariable("procId") long procId, @AuthenticationPrincipal User user) {
        return votingService.acceptInvitation(user, procId, true);
    }

    @GetMapping("/api/proc/{procId}/reject")
    public ApiResponse rehectInvite(@PathVariable("procId") long procId, @AuthenticationPrincipal User user) {
        return votingService.acceptInvitation(user, procId, false);
    }

    @PostMapping("/api/proc/{procId}/message")
    public ApiResponse postMessage(@PathVariable("procId") long procId, @AuthenticationPrincipal User user, @RequestBody MessageRequest messageRequest) {
        return messageService.save(procId, user, messageRequest);
    }

}
