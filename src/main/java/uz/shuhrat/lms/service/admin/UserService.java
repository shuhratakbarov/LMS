package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.LoginForm;
import uz.shuhrat.lms.dto.form.user.CreateUserForm;

import java.util.UUID;

public interface UserService {
    ResponseDto<?> login(LoginForm form) throws Exception;
    ResponseDto<?> logout(String token);
    ResponseDto<?> refresh(String refreshToken);

    ResponseDto<?> currentUserInfo() throws Exception;

    ResponseDto<?> save(CreateUserForm userForm) throws Exception;

    ResponseDto<?> edit(UUID id, CreateUserForm userForm) throws Exception;

    ResponseDto<?> delete(UUID id) throws Exception;

    ResponseDto<?> findTeachersForSelect();

    ResponseDto<?> searchStudent(String username) throws Exception;

    ResponseDto<?> getStudentsOfGroup(Long groupId, int page, int size) throws Exception;

    ResponseDto<?> getUserList(String role, String searching, int page, int size) throws Exception;
}
