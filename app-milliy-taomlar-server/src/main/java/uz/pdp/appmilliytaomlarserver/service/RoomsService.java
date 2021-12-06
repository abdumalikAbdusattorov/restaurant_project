package uz.pdp.appmilliytaomlarserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.pdp.appmilliytaomlarserver.entity.Rooms;
import uz.pdp.appmilliytaomlarserver.exception.ResourceNotFoundException;
import uz.pdp.appmilliytaomlarserver.payload.*;
import uz.pdp.appmilliytaomlarserver.repository.AttachmentRepository;
import uz.pdp.appmilliytaomlarserver.repository.RoomsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomsService {
    @Autowired
    RoomsRepository roomsRepository;

    @Autowired
    AttachmentRepository attachmentRepository;

    public ApiResponse saveOrEditRooms(ReqRoom reqRoom) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setMessage("Saved");
            apiResponse.setSuccess(true);
            Rooms rooms = new Rooms();
            if (reqRoom.getId() != null) {
                rooms = roomsRepository.findById(reqRoom.getId()).orElseThrow(() -> new ResourceNotFoundException("room", "id", reqRoom.getId()));
                apiResponse.setMessage("Edited");
            }
            rooms.setNumber(reqRoom.getNumber());
            rooms.setPrice(reqRoom.getPrice());
            rooms.setDescriptionUz(reqRoom.getDescriptionUz());
            rooms.setDescriptionRu(reqRoom.getDescriptionRu());
            if (reqRoom.getAttachment() != null) {
                rooms.setAttachment(attachmentRepository.findById(reqRoom.getAttachment()).orElseThrow(() -> new ResourceNotFoundException("attechment", "id", reqRoom.getAttachment())));
            }
            roomsRepository.save(rooms);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponse deleteRoom(Integer id) {
        try {
            roomsRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ResRoom getResRoom(Rooms rooms) {
        ResRoom resRoom = new ResRoom();
        resRoom.setId(rooms.getId());
        resRoom.setNumber(rooms.getNumber());
        resRoom.setDescriptionUz(rooms.getDescriptionUz());
        resRoom.setDescriptionRu(rooms.getDescriptionRu());
        resRoom.setPrice(rooms.getPrice());
        if (rooms.getAttachment() != null) {
            resRoom.setAttachment(rooms.getAttachment().getId());
        }
        return resRoom;
    }

    public ApiResponseModel getAll() {
        List<ResRoom> resRoomList = new ArrayList<>();
        List<Rooms> all = roomsRepository.findAll();
        for (Rooms rooms : all) {
            resRoomList.add(getResRoom(rooms));
        }
        return new ApiResponseModel(true, "Ok", resRoomList);
    }

    public ResPageable getAllByPageable(Integer page, Integer size) {
        List<ResRoom> resRoomList = new ArrayList<>();
        Page<Rooms> roomsPage = roomsRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "id"));
        return new ResPageable(roomsPage.getContent().stream().map(this::getResRoom).collect(Collectors.toList()), roomsPage.getTotalElements(), page);
    }
}
