package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.db.domain.User;
import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.UserRequestDto;

import java.time.Instant;
import java.util.UUID;

public interface UserService {
    GeneralResponseDto<?> currentUserInfo() throws Exception;

    User getById(UUID userId) throws Exception;

    GeneralResponseDto<?> save(UserRequestDto userForm) throws Exception;

    GeneralResponseDto<?> edit(UUID id, UserRequestDto userForm) throws Exception;

    GeneralResponseDto<?> delete(UUID id) throws Exception;

    GeneralResponseDto<?> findTeachersToSelect();

    GeneralResponseDto<?> searchStudent(String username) throws Exception;

    GeneralResponseDto<?> getStudentsOfGroup(Long groupId, int page, int size) throws Exception;

    GeneralResponseDto<?> getUserList(String role, String searching, int page, int size) throws Exception;

    Instant updateLastSeen(String username);
}
