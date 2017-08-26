package travel.model;

import org.apache.commons.lang3.StringUtils;

public class Validator {

    public boolean validate(User user, boolean isUpdate) {
        boolean idValid = (user.id == Constants.INT_FIELD_MISSING && isUpdate) || (user.id != Constants.INT_FIELD_MISSING);
        boolean firstNameValid = (user.firstName == null && isUpdate) || (!StringUtils.isEmpty(user.firstName) && user.firstName.length() <= 50);
        boolean lastNameValid = (user.lastName == null && isUpdate) || (!StringUtils.isEmpty(user.lastName) && user.lastName.length() <= 50);
        boolean emailValid = (user.email == null && isUpdate) || (!StringUtils.isEmpty(user.email) && user.email.length() <= 100);
        boolean genderValid = (user.gender == null && isUpdate) || (user.gender != null && ('m' == user.gender || 'f' == user.gender));
        boolean birthDateValid = isUpdate || user.birthDate != Constants.INT_FIELD_MISSING;
        return idValid && firstNameValid && lastNameValid && emailValid && genderValid && birthDateValid;
    }

    public boolean validate(Location location, boolean isUpdate) {
        boolean idValid = (location.id == Constants.INT_FIELD_MISSING && isUpdate) || (location.id != Constants.INT_FIELD_MISSING);
        boolean countryValid = (location.country == null && isUpdate) || (!StringUtils.isEmpty(location.country) && location.country.length() <= 50);
        boolean cityValid = (location.city == null && isUpdate) || (!StringUtils.isEmpty(location.city) && location.city.length() <= 50);
        return idValid && countryValid && cityValid;
    }

    public boolean validate(Visit visit, boolean isUpdate) {
        boolean idValid = (visit.id == Constants.INT_FIELD_MISSING && isUpdate) || (visit.id != Constants.INT_FIELD_MISSING);
        boolean visitedValid = isUpdate || visit.visited != Constants.INT_FIELD_MISSING;
        boolean markValid = (visit.mark == Constants.INT_FIELD_MISSING && isUpdate) || (visit.mark != Constants.INT_FIELD_MISSING && visit.mark >= 0 && visit.mark <= 5);
        return idValid && visitedValid && markValid;
    }
}
