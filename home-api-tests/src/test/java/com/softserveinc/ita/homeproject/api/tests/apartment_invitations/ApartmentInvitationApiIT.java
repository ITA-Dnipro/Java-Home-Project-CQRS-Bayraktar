package com.softserveinc.ita.homeproject.api.tests.apartment_invitations;

import com.softserveinc.ita.homeproject.api.tests.utils.ApiClientUtil;
import com.softserveinc.ita.homeproject.client.ApiException;
import com.softserveinc.ita.homeproject.client.api.ApartmentApi;
import com.softserveinc.ita.homeproject.client.api.ApartmentInvitationApi;
import com.softserveinc.ita.homeproject.client.api.CooperationApi;
import com.softserveinc.ita.homeproject.client.api.HouseApi;
import com.softserveinc.ita.homeproject.client.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.softserveinc.ita.homeproject.api.tests.utils.CreateSampleUtil.createAddress;
import static com.softserveinc.ita.homeproject.api.tests.utils.mail.mock.InvitationApprovalUtil.approveInvitation;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ApartmentInvitationApiIT {

    private final ApartmentInvitationApi apartmentInvitationApi = new ApartmentInvitationApi(ApiClientUtil.getCooperationAdminClient());

    private final ApartmentApi apartmentApi = new ApartmentApi(ApiClientUtil.getCooperationAdminClient());

    private final HouseApi houseApi = new HouseApi(ApiClientUtil.getCooperationAdminClient());

    private final CooperationApi cooperationApi = new CooperationApi(ApiClientUtil.getCooperationAdminClient());

    private final int waitTime = 5000;

    @Test
    void deactivateAcceptedApartmentInvitationTest() throws ApiException, IOException, InterruptedException {
        Entry<ReadApartmentInvitation, ReadApartment> result = createApartmentInvitation();
        TimeUnit.MILLISECONDS.sleep(waitTime);
        approveInvitation(result.getKey().getEmail());
        assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
                .isThrownBy(() -> apartmentInvitationApi.deleteInvitation(result.getValue().getId(), result.getKey().getId()));
    }

    private Entry<ReadApartmentInvitation, ReadApartment> createApartmentInvitation() throws ApiException {
        CreateApartmentInvitation createApartmentInvitation = new CreateApartmentInvitation();
        createApartmentInvitation.setOwnershipPart(BigDecimal.valueOf(0.3));
        createApartmentInvitation.email("test.receive.messages@gmail.com");
        createApartmentInvitation.setType(InvitationType.APARTMENT);
        ReadApartment apartment = createApartment();
        ReadApartmentInvitation apartmentInvitation = apartmentInvitationApi.createInvitation(apartment.getId(), createApartmentInvitation);
        return new AbstractMap.SimpleEntry<>(apartmentInvitation, apartment);
    }

    private ReadApartment createApartment() throws ApiException {
        ReadHouse house = createHouse();
        CreateApartment apartment = new CreateApartment()
                .area(BigDecimal.valueOf(72.5))
                .number("15");
        return apartmentApi.createApartment(house.getId(), apartment);
    }

    private ReadHouse createHouse() throws ApiException {
        ReadCooperation cooperation = createCooperation();
        CreateHouse house = new CreateHouse()
                .adjoiningArea(500)
                .houseArea(BigDecimal.valueOf(500.0))
                .quantityFlat(50)
                .address(createAddress());
        return houseApi.createHouse(cooperation.getId(), house);
    }

    private ReadCooperation createCooperation() throws ApiException {
        CreateCooperation cooperation = new CreateCooperation()
                .name(RandomStringUtils.randomAlphabetic(10).concat(" Cooperation"))
                .usreo(RandomStringUtils.randomNumeric(8))
                .iban("UA".concat(RandomStringUtils.randomNumeric(27)))
                .adminEmail(RandomStringUtils.randomAlphabetic(10).concat("@gmail.com"))
                .address(createAddress());
        return cooperationApi.createCooperation(cooperation);
    }
}
