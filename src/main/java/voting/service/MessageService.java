package voting.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import voting.exception.ResourceNotFoundException;
import voting.model.auth.User;
import voting.model.chat.Message;
import voting.model.chat.MessageDTO;
import voting.model.chat.MessageRequest;
import voting.model.common.ApiResponse;
import voting.model.vote.VotingProcess;
import voting.repositories.MessageRepository;
import voting.repositories.VotingProcessRepoitory;

import java.time.Instant;

@Service
public class MessageService {
    static private String channelURL = "/topic/chat/";

    private SimpMessagingTemplate simpMessagingTemplate;
    private VotingProcessRepoitory votingProcessRepoitory;
    private MessageRepository messageRepository;
    private ModelMapper modelMapper;

    @Autowired
    public MessageService(SimpMessagingTemplate simpMessagingTemplate, VotingProcessRepoitory votingProcessRepoitory, MessageRepository messageRepository, ModelMapper modelMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.votingProcessRepoitory = votingProcessRepoitory;
        this.messageRepository = messageRepository;
        this.modelMapper = modelMapper;
    }

    public ApiResponse save(long id, User user, MessageRequest messageRequest) {
        VotingProcess votingProcess = votingProcessRepoitory.findById(id).orElseThrow(() -> new ResourceNotFoundException("voting process ", "id", id));
        Message message = new Message();
        message.setUser(user);
        message.setContent(messageRequest.getContent());
        message.setTimestamp(Instant.now());
        messageRepository.save(message);
        votingProcess.getMessages().add(message);
        votingProcessRepoitory.save(votingProcess);
        sendMessage(id, message);
        return new ApiResponse(true, "Message sent successfully");
    }

    private void sendMessage(long procId, Message message) {
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        messageDTO.setUsername(message.getUser().getUsername());
        simpMessagingTemplate.convertAndSend(channelURL + procId, messageDTO);
    }
}
