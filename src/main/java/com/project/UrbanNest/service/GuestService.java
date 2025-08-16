package com.project.UrbanNest.service;

import java.util.List;
import com.project.UrbanNest.dto.GuestDto;

public interface GuestService {

    List<GuestDto> getAllGuests();

    void updateGuest(Long guestId,GuestDto guestDto);

    void deleteGuest(Long guestId);

    GuestDto addNewGuest(GuestDto guestDto);

}
