package be.fooda.backend.user.model.twilio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    private List<String> numbers;
    private String message;
}