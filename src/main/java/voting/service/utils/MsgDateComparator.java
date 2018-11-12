package voting.service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import voting.model.chat.Message;

import java.util.Comparator;

@Data
@AllArgsConstructor
public class MsgDateComparator implements Comparator<Message> {
    private boolean reversed;

    @Override
    public int compare(Message o1, Message o2) {

        return reversed ? o1.getTimestamp().compareTo(o2.getTimestamp()) : o2.getTimestamp().compareTo(o1.getTimestamp());
    }
}