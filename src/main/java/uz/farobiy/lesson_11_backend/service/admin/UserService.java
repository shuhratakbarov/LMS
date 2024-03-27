package uz.farobiy.lesson_11_backend.service.admin;

import uz.farobiy.lesson_11_backend.dto.ResponseDto;
import uz.farobiy.lesson_11_backend.dto.form.LoginForm;
import uz.farobiy.lesson_11_backend.dto.form.user.CreateUserForm;

import java.util.List;
import java.util.UUID;

public interface UserService {
    ResponseDto signin(LoginForm form) throws Exception;

    ResponseDto currentUserInfo() throws Exception;

    ResponseDto save(CreateUserForm userForm) throws Exception;

    ResponseDto edit(UUID id, CreateUserForm userForm) throws Exception;

    ResponseDto delete(UUID id) throws Exception;

    ResponseDto findAllByRoleId(Long roleIds, String isActive, int page, int size) throws Exception;

    ResponseDto findTeachersForSelect();

    ResponseDto searchStudent(String username) throws Exception;

    ResponseDto getStudentsOfGroup(Long groupId, int page, int size) throws Exception;

    ResponseDto search(Long roleId, String isActive, String searching, int page, int size) throws Exception;

}
