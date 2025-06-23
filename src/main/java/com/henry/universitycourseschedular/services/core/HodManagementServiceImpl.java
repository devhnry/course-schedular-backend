package com.henry.universitycourseschedular.services.core;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.InviteStatus;
import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.exceptions.ResourceNotFoundException;
import com.henry.universitycourseschedular.models.AppUser;
import com.henry.universitycourseschedular.models.Department;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.HodManagementDto;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.DepartmentRepository;
import com.henry.universitycourseschedular.repositories.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.henry.universitycourseschedular.utils.ApiResponseUtil.buildSuccessResponse;

@Service @Slf4j @RequiredArgsConstructor
public class HodManagementServiceImpl implements HodManagementService {

    private final InvitationRepository invitationRepo;
    private final AppUserRepository userRepo;
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

            boolean expired = inv.isExpiredOrUsed() || inv.getExpiryDate().isBefore(LocalDateTime.now());

            InviteStatus status = (user != null && Boolean.TRUE.equals(user.getAccountVerified()))
                    ? InviteStatus.ACCEPTED
                    : (!expired ? InviteStatus.PENDING : InviteStatus.EXPIRED);

            Department department = inv.getDepartment();
            String departmentCode = department != null ? department.getCode() : null;
            String buildingCode = (department != null && department.getCollegeBuilding() != null)
                    ? department.getCollegeBuilding().getCode()
                    : null;
            String departmentName = department != null ? department.getName() : null;

            return HodManagementDto.builder()
                    .userId(user != null ? user.getUserId() : null)
                    .emailAddress(inv.getEmailAddress())
                    .departmentCode(departmentCode)
                    .departmentName(departmentName)
                    .collegeBuildingCode(buildingCode)
                    .status(status)
                    .invitedAt(inv.getCreatedAt())
                    .expiresAt(inv.getExpiryDate())
                    .accountVerified(user != null && user.getAccountVerified())
                    .writeAccess(user != null && Boolean.TRUE.equals(user.getWriteAccess()))
                    .build();
        }).toList();

        return buildSuccessResponse("All HOD invitations & users", StatusCodes.ACTION_COMPLETED, dtos);
    }

    @Override
    public DefaultApiResponse<HodManagementDto> updateHodAccess(String userId, Boolean grantWrite) {
        AppUser u = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found: " + userId));
        u.setWriteAccess(grantWrite);
        userRepo.save(u);

        HodManagementDto dto = HodManagementDto.builder()
                .emailAddress(u.getEmailAddress())
                .departmentCode(u.getDepartment().getCode())
                .departmentName(u.getDepartment().getName())
                .collegeBuildingCode(u.getDepartment().getCollegeBuilding().getCode())
                .accountVerified(u.getAccountVerified())
                .writeAccess(u.getWriteAccess())
                .status(InviteStatus.ACCEPTED)
                .build();

        return buildSuccessResponse("Write access updated", StatusCodes.ACTION_COMPLETED ,dto);
    }

    @Override
    public DefaultApiResponse<?> deleteHod(String userIdOrEmail) {
        AppUser user = userRepo.findById(userIdOrEmail).orElse(null);

        if (user != null) {
            userRepo.delete(user);
            return buildSuccessResponse("HOD deleted", StatusCodes.ACTION_COMPLETED, null);
        }

        var invitationOpt = invitationRepo.findByEmailAddress(userIdOrEmail);
        if (invitationOpt.isEmpty()) {
            throw new ResourceNotFoundException("No HOD or invitation found with ID/email: " + userIdOrEmail);
        }

        var invitation = invitationOpt.get();
        boolean isExpired = invitation.isExpiredOrUsed() || invitation.getExpiryDate().isBefore(LocalDateTime.now());

        if (isExpired) {
            invitationRepo.delete(invitation);
            return buildSuccessResponse("Expired unaccepted invitation deleted", StatusCodes.ACTION_COMPLETED, null);
        }

        return buildSuccessResponse("Cannot delete HOD invitation still pending", StatusCodes.ACTION_NOT_PERMITTED, null);
    }
}
