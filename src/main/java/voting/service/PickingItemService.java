package voting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import voting.exception.AppException;
import voting.model.common.ApiResponse;
import voting.model.vote.PickingItem;
import voting.model.vote.PickingItemsRequest;
import voting.repositories.PickItemRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PickingItemService {
    private final PickItemRepository pickItemRepository;

    @Autowired
    public PickingItemService(PickItemRepository pickItemRepository) {
        this.pickItemRepository = pickItemRepository;
    }

    public List<PickingItem> getItemsWithNameStartingWith(String query) {
        return pickItemRepository.findAllByNameStartingWith(query);
    }

    public ApiResponse addNewItems(PickingItemsRequest pickingItemsRequest) {
        if (pickingItemsRequest.getNames() == null || pickingItemsRequest.getUrls() == null)
            return new ApiResponse(false, "Form is not filled correctly");

        List<String> names = pickingItemsRequest.getNames().strip().lines().map(String::trim).filter(name -> !name.isBlank()).collect(Collectors.toList());
        List<String> urls = pickingItemsRequest.getUrls().strip().lines().map(String::trim).filter(url -> !url.isBlank()).collect(Collectors.toList());


        Optional<ApiResponse> nameResponse = validateNames(names);
        if(nameResponse.isPresent()) {
            return nameResponse.get();
        }

        Optional<ApiResponse> urlResponse = validateUrls(urls);
        if(urlResponse.isPresent()) {
            return urlResponse.get();
        }

        for (int i = 0; i < names.size(); ++i) {
            log.info("i = {}, {} {}", i, names.get(i), urls.get(i));
            PickingItem pickingItem = new PickingItem();
            pickingItem.setName(names.get(i));
            pickingItem.setImgUrl(urls.get(i));
            pickItemRepository.save(pickingItem);
        }
        return new ApiResponse(true, "Picking items added successfully.");
    }

    public ApiResponse checkItemNames(String names) {
        List<String> nameList = names.strip().lines().map(String::trim)
                .filter(name -> !name.isBlank()).collect(Collectors.toList());


        return validateNames(nameList).orElse(new ApiResponse(true, "Names available."));
    }

    public ApiResponse checkItemUrls(String urls) {
        List<String> urlList = urls.strip().lines().map(String::trim)
                .filter(url -> !url.isBlank()).collect(Collectors.toList());


        return validateUrls(urlList).orElse(new ApiResponse(true, "URLs available."));
    }

    private Optional<ApiResponse> validateNames(List<String> nameList) {
        return nameList.stream()
                .map(name -> {
                    Optional<PickingItem> userOptional = pickItemRepository.findByName(name);
                    if (userOptional.isPresent()) {
                        return new ApiResponse(false, "Item with name" + name +" already exists");
                    } else {
                        return new ApiResponse(true, "Name available.");
                    }
                }).filter(apiResponse -> !apiResponse.isSuccess())
                .findFirst();
    }

    private Optional<ApiResponse> validateUrls(List<String> urls) {
        return urls.stream()
                .map(url -> {
                    if(!validateImageUrl(url))
                        return new ApiResponse(false, "Url " + url + " is not correct or doesnt contain image.");
                    Optional<PickingItem> userOptional = pickItemRepository.findByImgUrl(url);
                    if (userOptional.isPresent()) {
                        return new ApiResponse(false, "Item with url" + url +" already exists");
                    } else {
                        return new ApiResponse(true, "Name available.");
                    }
                }).filter(apiResponse -> !apiResponse.isSuccess())
                .findFirst();
    }

    private boolean validateImageUrl(String imgUrl) {
        Image image;
        try {
            image = ImageIO.read(new URL(imgUrl));
        } catch (IOException e) {
            return false;
        }
        return image != null;
    }
}
