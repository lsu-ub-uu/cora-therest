package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.bookkeeper.data.DataElement;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.bookkeeper.validator.ValidationAnswer;

/**
 *
 *
 * @author <a href="mailto:madeleine.kennback@ub.uu.se">Madeleine Kennbäck</a>
 * @version $Revision$, $Date$, $Author$
 */
public class DataValidatorReturnNullPointer implements DataValidator {
    @Override
    public ValidationAnswer validateData(String metadataId, DataElement dataGroup) {
        throw new NullPointerException();
    }
}
