package com.example.backend.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Log4j2
public class RoomController {

    private final ChatRoomRepository repository;

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    @ResponseBody
    public List<ChatRoomDTO> rooms(){
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        log.info("# All Chat Rooms");
        ModelAndView mv = new ModelAndView("chat/rooms");
        System.out.println(chatMessageDTO.getProfileUrl());
        return repository.findAllRooms();
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    @ResponseBody
    public ResponseEntity<String> create(@RequestParam String name, RedirectAttributes rttr){

        log.info("# Create Chat Room , name: " + name);
        rttr.addFlashAttribute("roomName", repository.createChatRoomDTO(name));
        return new ResponseEntity<>("채팅방이 개설되었습니다.", HttpStatus.OK);
    }

//    //채팅방 조회
//    @GetMapping("/room")
//    @ResponseBody
//    public void getRoom(String roomId, Model model){
//
//        log.info("# get Chat Room, roomID : " + roomId);
//        ChatRoomDTO roomById = repository.findRoomById(roomId);
//    }
}
