package voting.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import voting.model.common.ApiResponse;
import voting.model.vote.ItemRequest;
import voting.model.vote.PickingItem;
import voting.model.vote.PickingItemsRequest;
import voting.service.PickingItemService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@CrossOrigin
public class PickingItemController {
    final private PickingItemService pickingItemService;

    @Autowired
    public PickingItemController(PickingItemService pickingItemService) {
        this.pickingItemService = pickingItemService;
    }

    @GetMapping("/api/items")
    public List<PickingItem> getItemsStartingWith(@RequestParam("q") String query) {
        return pickingItemService.getItemsWithNameStartingWith(query);
    }

    @PostMapping("/api/items")
    public ApiResponse addNewItems(@RequestBody PickingItemsRequest pickingItemsRequest) {
        log.info("request {}", pickingItemsRequest);
        return pickingItemService.addNewItems(pickingItemsRequest);
    }

    @PostMapping("/api/item/checkName")
    public ApiResponse checkUsername(@RequestBody ItemRequest itemRequest) {
        return pickingItemService.checkItemNames(itemRequest.getField());

    }

    @PostMapping("/api/item/checkImgUrl")
    public ApiResponse checkImageUrl(@RequestBody ItemRequest itemRequest) {
        return pickingItemService.checkItemUrls(itemRequest.getField());

    }
}
