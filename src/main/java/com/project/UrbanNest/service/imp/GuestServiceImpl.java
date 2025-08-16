package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.entity.Guest;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.dto.GuestDto;
import com.project.UrbanNest.repository.GuestRepository;
import com.project.UrbanNest.service.GuestService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.UrbanNest.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<GuestDto> getAllGuests() {

        User user=getCurrentUser();
        log.info("Fetching all guest of user with id: {}",user.getId());
        List<Guest> guests=guestRepository.findByUser(user);
        return guests.stream()
                .map(guest -> modelMapper.map(guest, GuestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateGuest(Long guestId, GuestDto guestDto) {
        log.info("Updating guest with ID: {}",guestId);
        Guest guest=guestRepository.findById(guestId).orElseThrow(()-> new EntityNotFoundException("Guest not found"));

        User user=getCurrentUser();
        if(!user.equals(guest.getUser())){
            throw new AccessDeniedException("You are not the owner of this guest");
        }

        modelMapper.map(guestDto,guest);
        guest.setUser(user);
        guest.setId(guestId);

        guestRepository.save(guest);
        log.info("Guest with ID: {} updated successfully",guestId);
    }

    @Override
    public void deleteGuest(Long guestId) {
        log.info("Deleting guest with ID: {}",guestId);
        Guest guest=guestRepository.findById(guestId)
                .orElseThrow(()-> new EntityNotFoundException("Guest not found"));

        User user=getCurrentUser();

        if(!user.equals(guest.getUser())){
            throw new AccessDeniedException("You are not the owner of this guest");
        }

        guestRepository.deleteById(guestId);
        log.info("Guest With ID: {} deleted successfully",guestId);
    }

    @Override
    public GuestDto addNewGuest(GuestDto guestDto) {
        log.info("Adding new guest : {}",guestDto);
        User user=getCurrentUser();
        Guest guest=modelMapper.map(guestDto,Guest.class);
        guest.setUser(user);
        Guest savedGuest=guestRepository.save(guest);
        log.info("Guest Added With ID: {}",savedGuest.getId());

        return modelMapper.map(savedGuest, GuestDto.class);
    }
}
