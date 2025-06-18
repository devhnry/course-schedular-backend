package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.InviteStatus;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.HodManagementDto;
import com.henry.universitycourseschedular.models.core.Department;
import com.henry.universitycourseschedular.models.user.AppUser;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j @RequiredArgsConstructor
public class HodManagementServiceImpl implements HodManagementService {

    private final InvitationRepository invitationRepo;
    private final AppUserRepository     userRepo;
    private final DepartmentRepository deptRepo;

    @Override
    public DefaultApiResponse<List<HodManagementDto>> listAllHods(Pageable pageable) {
        var invites = invitationRepo.findAll(pageable);
        log.info("Page {} of {} containing {} invitations",
                invites.getNumber() + 1,
                invites.getTotalPages(),
                invites.getNumberOfElements());

        // build a map of onboarded HODs by email
        var usersByEmail = userRepo.findAllByRole(Role.HOD)
                .stream()
                .collect(Collectors.toMap(AppUser::getEmailAddress, u -> u));

        List<HodManagementDto> dtos = invites.stream().map(inv -> {
            AppUser user = usersByEmail.get(inv.getEmailAddress());

            boolean expired = inv.isExpiredOrUsed()
                    || inv.getExpiryDate().isBefore(ZonedDateTime.now());

            InviteStatus status = (user != null && Boolean.TRUE.equals(user.getAccountVerified()))
                    ? InviteStatus.ACCEPTED
                    : (!expired ? InviteStatus.PENDING : InviteStatus.EXPIRED);

            // Safely parse department ID
            String deptIdStr = inv.getDepartmentId();
            Long deptId = null;
            if (deptIdStr != null) {
                try {
                    deptId = Long.parseLong(deptIdStr);
                } catch (NumberFormatException ignored) {
                    // leave deptId null
                }
            }

            // Lookup department name if we have a valid ID
            String deptName = null;
            if (deptId != null) {
                deptName = deptRepo.findById(deptId)
                        .map(Department::getName)
                        .orElse(null);
            }

            assert user != null;
            return HodManagementDto.builder()
                    .emailAddress(inv.getEmailAddress())
                    .departmentId(deptIdStr)
                    .departmentName(deptName)
                    .status(status)
                    .invitedAt(inv.getCreatedAt())
                    .expiresAt(inv.getExpiryDate())
                    .build();
        }).toList();

        return buildSuccessResponse("All HOD invitations & users",
                StatusCodes.ACTION_COMPLETED,
                dtos);
    }

    @Override
    public DefaultApiResponse<HodManagementDto> updateHodAccess(String userId, Boolean grantWrite) {
        AppUser u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found: " + userId));
        u.setWriteAccess(grantWrite);
        userRepo.save(u);

        // map back to DTO (omitting invitation timestamps here)
        HodManagementDto dto = HodManagementDto.builder()
                .emailAddress(u.getEmailAddress())
                .departmentId(u.getDepartment().getId().toString())
                .departmentName(u.getDepartment().getName())
                .accountVerified(u.getAccountVerified())
                .writeAccess(u.getWriteAccess())
                .status(InviteStatus.ACCEPTED)
                .build();

        return buildSuccessResponse("Write access updated", StatusCodes.ACTION_COMPLETED ,dto);
    }

    @Override
    public DefaultApiResponse<?> deleteHod(String userId) {
        AppUser u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found: " + userId));
        userRepo.delete(u);
        return buildSuccessResponse("HOD deleted", StatusCodes.ACTION_COMPLETED, null);
    }
}