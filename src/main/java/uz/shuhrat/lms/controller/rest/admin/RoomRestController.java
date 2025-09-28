package uz.shuhrat.lms.controller.rest.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.shuhrat.lms.db.domain.Room;
import uz.shuhrat.lms.db.repository.RoomRepository;
import uz.shuhrat.lms.dto.request.RoomRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/room")
public class RoomRestController {
    private final RoomRepository roomRepository;

    @Autowired
    public RoomRestController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRooms() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> rooms = roomRepository.findAll().stream()
                    .map(room -> {
                        Map<String, Object> roomData = new HashMap<>();
                        roomData.put("id", room.getId());
                        roomData.put("name", room.getName());
                        roomData.put("description", room.getDescription());
                        return roomData;
                    })
                    .collect(Collectors.toList());
            response.put("success", true);
            response.put("data", rooms);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch rooms: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Room room = new Room();
            room.setName(roomRequestDto.name());
            room.setDescription(roomRequestDto.description());
            room = roomRepository.save(room);
            response.put("success", true);
            response.put("message", "Room created successfully");
            response.put("data", Map.of("id", room.getId(), "name", room.getName()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create room: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoom(
            @PathVariable("id") Long id,
            @RequestBody RoomRequestDto roomRequestDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Room room = roomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            room.setName(roomRequestDto.name());
            room.setDescription(roomRequestDto.description());
            room = roomRepository.save(room);
            response.put("success", true);
            response.put("message", "Room updated successfully");
            response.put("data", Map.of("id", room.getId(), "name", room.getName(), "description", room.getDescription()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update room: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoom(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Room room = roomRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            roomRepository.delete(room);
            response.put("success", true);
            response.put("message", "Room deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete room: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}