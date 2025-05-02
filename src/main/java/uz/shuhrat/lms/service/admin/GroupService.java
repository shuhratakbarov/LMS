package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.ResponseDto;
import uz.shuhrat.lms.dto.form.CreateGroupForm;

import java.util.UUID;

public interface GroupService {
    ResponseDto<?> save(CreateGroupForm form) throws Exception;

    ResponseDto<?> edit(Long id, CreateGroupForm form) throws Exception;

    ResponseDto<?> delete(Long id) throws Exception;

    ResponseDto<?> getGroupList(String searching, int page, int size) throws Exception;

    ResponseDto<?> findGroupsByTeacherId(UUID teacherId) throws Exception;

    ResponseDto<?> getGroupsAndTeacherByStudentId(UUID userId) throws Exception;

    ResponseDto<?> findAllByCourseId(Long courseId, int page, int size) throws Exception;

    ResponseDto<?> addStudentToGroup(UUID studentId, Long groupId) throws Exception;

    ResponseDto<?> removeStudentFromGroup(UUID studentId, Long groupId);

    ResponseDto<?> getGroupIdAndName();
}
