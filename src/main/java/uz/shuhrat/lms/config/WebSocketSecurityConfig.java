//package uz.shuhrat.lms.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
//import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
//import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
//
//@Configuration
//@EnableWebSocketSecurity
//public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//
//    @Override
//    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//        messages
//                .nullDestMatcher().permitAll() // allow CONNECT/DISCONNECT frames
//                .simpDestMatchers("/app/**").authenticated()
//                .simpSubscribeDestMatchers("/topic/**", "/queue/**").authenticated()
//                .anyMessage().denyAll();
//    }
//
//    @Override
//    protected boolean sameOriginDisabled() {
//        return true; // Disable same-origin for SockJS/React client
//    }
//}
