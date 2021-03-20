package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

public class UserUpdateValidator  implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void initialize(UserUpdateValid ann) {
		
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

		@SuppressWarnings("unchecked")
		Map<String, String> uriVars = (Map<String, String>) this.request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Long userId = Long.parseLong(uriVars.get("id"));

		List<FieldMessage> list = new ArrayList<FieldMessage>();

		User user = this.userRepository.findByEmail(dto.getEmail());

		if (user != null && userId != user.getId()) {
			list.add(new FieldMessage("email", "Esse email já existe"));
		}

		list.forEach(e -> {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
			       .addConstraintViolation();
		});

		return list.isEmpty();

	}
}
