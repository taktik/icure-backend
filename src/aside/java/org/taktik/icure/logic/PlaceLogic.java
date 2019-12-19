package org.taktik.icure.logic;

import org.taktik.icure.entities.Place;
import org.taktik.icure.exceptions.DeletionException;

import java.util.List;

public interface PlaceLogic extends EntityPersister<Place, String>{
    Place createPlace(Place place);

    List<String> deletePlace(List<String> ids) throws DeletionException;

    Place getPlace(String place);

    Place modifyPlace(Place place);

}
