package rs.ac.ftn.isa.backend.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import rs.ac.ftn.isa.backend.domain.model.User;
import rs.ac.ftn.isa.backend.dto.StreamingChatMessageRequest;
import rs.ac.ftn.isa.backend.dto.StreamingChatMessageResponse;
import rs.ac.ftn.isa.backend.service.UserService;
import rs.ac.ftn.isa.backend.util.TokenUtils;

@Controller
public class StreamingChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final TokenUtils tokenUtils;
    private final UserService userService;

    public StreamingChatController(SimpMessagingTemplate messagingTemplate,
                                   TokenUtils tokenUtils,
                                   UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
    }

    @MessageMapping("/video/{videoId}/chat")
    public void sendMessage(@DestinationVariable Long videoId, StreamingChatMessageRequest request) {
        String token = request.getToken();
        if (token == null || token.isBlank()) {
            return;
        }

        String username;
        try {
            username = tokenUtils.getUsernameFromToken(token);
        } catch (Exception e) {
            return;
        }

        if (username == null) {
            return;
        }

        User user;
        try {
            user = userService.findByEmail(username);
        } catch (Exception e) {
            return;
        }

        if (!tokenUtils.validateToken(token, user)) {
            return;
        }

        String authorName = formatDisplayName(user);
        String text = request.getText();
        if (text == null) {
            text = "";
        }
        text = text.trim();
        if (text.isEmpty()) {
            return;
        }

        long timestamp = System.currentTimeMillis();
        StreamingChatMessageResponse response = new StreamingChatMessageResponse(authorName, text, timestamp);
        messagingTemplate.convertAndSend("/topic/video/" + videoId, response);
    }

    private static String formatDisplayName(User user) {
        if (user.getFirstName() != null && !user.getFirstName().isBlank()
                && user.getLastName() != null && !user.getLastName().isBlank()) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return user.getEmail() != null ? user.getEmail() : "User";
    }
}
