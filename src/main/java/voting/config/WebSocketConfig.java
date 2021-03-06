package voting.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.messaging.access.intercept.ChannelSecurityInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.*;
import voting.model.auth.User;

import java.security.Principal;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserServiceDetails customUserServiceDetails;

    @Autowired
    public WebSocketConfig(JwtTokenProvider jwtTokenProvider, CustomUserServiceDetails customUserServiceDetails) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserServiceDetails = customUserServiceDetails;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/voting-socket").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor(){
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                Principal principal = null;

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    String header = accessor.getFirstNativeHeader("Authorization");

                    log.info("Header auth token: " + header);

                    String jwt = JwtTokenProvider.getToken(header);

                    log.info("Token only : " + jwt);


                    if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);

                        User user = (User) customUserServiceDetails.loadUserById(userId);

                        principal = user == null ? null : new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                        if (Objects.isNull(principal))
                            return null;

                        accessor.setUser(principal);
                    }

                }

                return message;
            }
        });

    }
}
