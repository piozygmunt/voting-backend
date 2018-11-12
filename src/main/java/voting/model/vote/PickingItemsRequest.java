package voting.model.vote;

import lombok.Data;

@Data
public class PickingItemsRequest {
    private String names;
    private String urls;
}
