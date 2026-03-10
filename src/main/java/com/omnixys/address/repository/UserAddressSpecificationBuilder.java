package com.omnixys.address.repository;

import com.omnixys.address.models.entity.UserAddress;
import com.omnixys.address.models.inputs.UserAddressFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserAddressSpecificationBuilder {

    public static Specification<UserAddress> build(UserAddressFilter filter) {

        return (root, query, cb) -> {

            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (filter.userId() != null) {
                predicates.add(cb.equal(root.get("userId"), filter.userId()));
            }

            if (filter.countryId() != null) {
                predicates.add(cb.equal(root.get("countryId"), filter.countryId()));
            }

            if (filter.cityId() != null) {
                predicates.add(cb.equal(root.get("cityId"), filter.cityId()));
            }

            if (filter.postalCodeId() != null) {
                predicates.add(cb.equal(root.get("postalCodeId"), filter.postalCodeId()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}