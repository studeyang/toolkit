package io.github.open.toolkit.commons.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.open.toolkit.commons.model.Model;
import io.github.open.toolkit.commons.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Base64;


public class AccessInfo implements Model, Serializable {

	private static final long serialVersionUID = 3180212862121943740L;
	private static final Logger LOG = LoggerFactory.getLogger(AccessInfo.class);

	public static final String HEADER_KEY = "ec-accessinfo";
	public static final String BIZ_ID_PREFIX_SEPARATOR = "-";
	
	@Deprecated
	public static final String OLD_HEADER_KEY = "access-info";
	
	private String accessId;
	private String ipAddress;
	private String userLoginId;
	private String referer;
	
	private String companyType;
	
	private String garageCompanyId;

	private String productStoreId;
	private String storeInternalId;
	
	public String getAccessId() {
		return accessId;
	}
	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserLoginId() {
		return userLoginId;
	}
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getReferer() {
		return referer;
	}
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	
	@JsonIgnore
	@Deprecated
	public String getCompanyId() {
		if (CompanyType.SUPPLIER.name().equals(companyType)) {
			return getSupplierCompanyId();
		} else {
			return getGarageCompanyId();
		}
	}

	public String getGarageCompanyId() {
		return this.garageCompanyId;
	}
	public void setGarageCompanyId(String garageCompanyId) {
		this.garageCompanyId = garageCompanyId;
	}

	@Deprecated
	public String getSupplierCompanyId() {
		return this.storeInternalId;
	}
	@Deprecated
	public void setSupplierCompanyId(String supplierCompanyId) {
		this.storeInternalId = supplierCompanyId;
	}
	public String getStoreInternalId() {
		return this.storeInternalId;
	}
	public void setStoreInternalId(String storeInternalId) {
		this.storeInternalId = storeInternalId;
	}
	
	public String getProductStoreId() {
		return this.productStoreId;
	}
	public void setProductStoreId(String productStoreId) {
		this.productStoreId = productStoreId;
	}

	/**
	 * 获取业务身份标识
	 * @return 如果没有则返回 null
	 */
	public String getBizIdentifier() {
		if (StringUtils.hasText(accessId) && accessId.contains(BIZ_ID_PREFIX_SEPARATOR)) {
			return accessId.substring(0, accessId.indexOf(BIZ_ID_PREFIX_SEPARATOR));
		}
		return null;
	}
	
	@Override
	public boolean validate() {
		
		if (!StringUtils.hasText(accessId)) {
			LOG.warn("invalid accessInfo!");
			return false;
		}
		
		return true;
	}
	
	public String toHeader() {
		return Base64.getEncoder().encodeToString((JsonUtil.serializeToBytes(this)));
	}
	
	@Deprecated
	public String toJson() {
		return JsonUtil.serializer(this);//兼容旧版
	}
	
	public static AccessInfo fromHeader(String accessInfoStr) {
		if (!StringUtils.hasText(accessInfoStr)) {
			accessInfoStr = System.getProperty(HEADER_KEY);
		}

		if (StringUtils.isEmpty(accessInfoStr)) {
			return null;
		}

		if (LOG.isDebugEnabled()) 
			LOG.debug("accessInfoStr: {}", accessInfoStr);
		
		AccessInfo accessInfo = null;
		
		if (accessInfoStr.startsWith("{")) { //兼容旧版
			accessInfo = JsonUtil.deserialize(accessInfoStr, AccessInfo.class);
			if (StringUtils.hasText(accessInfo.getProductStoreId())) {
				accessInfo.companyType = CompanyType.SUPPLIER.name();
			} else {
				accessInfo.companyType = CompanyType.GARAGE.name();
			}
		} else {
			accessInfo = JsonUtil.deserialize(Base64.getDecoder().decode(accessInfoStr), AccessInfo.class);
		}
		
		return accessInfo;
	}
	
	public static AccessInfo buildNamedAccessInfo(String name) {
		AccessInfo namedAccessInfo = new AccessInfo();
		namedAccessInfo.setAccessId("-----");
		namedAccessInfo.setReferer(name);
		return namedAccessInfo;
	}
	
	public enum CompanyType {
	    SUPPLIER, //供应商
	    GARAGE, //维修厂
	}

}
