package net.otterbase.oframework.auth.vo;

import org.springframework.security.core.userdetails.UserDetails;

public interface SignOnInterface {

	UserDetails toSignedDetails();

}
