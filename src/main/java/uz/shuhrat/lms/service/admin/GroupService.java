package uz.shuhrat.lms.service.admin;

import uz.shuhrat.lms.dto.GeneralResponseDto;
import uz.shuhrat.lms.dto.request.GroupRequestDto;

import java.util.UUID;

public interface GroupService {
    GeneralResponseDto<?> save(GroupRequestDto form) throws Exception;

    GeneralResponseDto<?> edit(Long id, GroupRequestDto form) throws Exception;

    GeneralResponseDto<?> delete(Long id) throws Exception;

    GeneralResponseDto<?> getGroupList(String searching, int page, int size) throws Exception;

    GeneralResponseDto<?> findGroupsByTeacherId(UUID teacherId) throws Exception;

    GeneralResponseDto<?> getGroupsAndTeacherByStudentId(UUID userId) throws Exception;

    GeneralResponseDto<?> findAllByCourseId(Long courseId, int page, int size) throws Exception;

    GeneralResponseDto<?> addStudentToGroup(UUID studentId, Long groupId) throws Exception;

    GeneralResponseDto<?> removeStudentFromGroup(UUID studentId, Long groupId);

    GeneralResponseDto<?> getGroupIdAndName();
}
