package listing;

import java.util.ArrayList;

public class Listing {
	private String title;
	private String fullTitle;
	private String body;
	private String price;
	private String email;
	private String finalized;
	private String category;
	private ArrayList<String> categories;
	private String shippingChoice;
	private ArrayList<String> shippingOptions;
	private String attribute;
	private ArrayList<String> attributes;
	private ArrayList<String> urls;
	private String time;
	private String condition;
	private String buyItNow;
	private String captcha;
	private String captchaImage;
	private String location;
	private String handlingTime;
	private String returns;
	private String reqSpecifics0;
	private String reqSpecifics1;
	private String reqSpecifics2;
	private String specifics;
	private String IP;
	
	public Listing() {
		this.title = "";
		this.fullTitle = "";
		this.body = "";
		this.price = "";
		this.email = "";
		this.finalized = "";
		this.category = "";
		this.categories = null;
		this.shippingChoice = "";
		this.shippingOptions = null;
		this.attribute = "";
		this.attributes = null;
		this.urls = null;
		this.time = "";
		this.condition = "";
		this.buyItNow = "";
		this.captcha = "";
		this.captchaImage = "";
		this.location = "";
		this.handlingTime = "";
		this.returns = "";
		this.reqSpecifics0 = "[]";
		this.reqSpecifics1 = "[]";
		this.reqSpecifics2 = "[]";
		this.specifics = "[]";
		this.IP = "";
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFullTitle() {
		return fullTitle;
	}
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFinalized() {
		return finalized;
	}
	public void setFinalized(String finalized) {
		this.finalized = finalized;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public ArrayList<String> getCategories() {
		return categories;
	}
	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
	public String getShippingChoice() {
		return shippingChoice;
	}
	public void setShippingChoice(String shippingChoice) {
		this.shippingChoice = shippingChoice;
	}
	public ArrayList<String> getShippingOptions() {
		return shippingOptions;
	}
	public void setShippingOptions(ArrayList<String> shippingOptions) {
		this.shippingOptions = shippingOptions;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public ArrayList<String> getAttributes() {
		return attributes;
	}
	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}
	public ArrayList<String> getUrls() {
		return urls;
	}
	public void setUrls(ArrayList<String> urls) {
		this.urls = urls;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getBuyItNow() {
		return buyItNow;
	}
	public void setBuyItNow(String buyItNow) {
		this.buyItNow = buyItNow;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public String getCaptchaImage() {
		return captchaImage;
	}
	public void setCaptchaImage(String captchaImage) {
		this.captchaImage = captchaImage;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getHandlingTime() {
		return handlingTime;
	}
	public void setHandlingTime(String handlingTime) {
		this.handlingTime = handlingTime;
	}
	public String getReturns() {
		return returns;
	}
	public void setReturns(String returns) {
		this.returns = returns;
	}
	
	public String getReqSpecifics0() {
		return reqSpecifics0;
	}
	public void setReqSpecifics0(String reqSpecifics0) {
		this.reqSpecifics0 = reqSpecifics0;
	}
	public String getReqSpecifics1() {
		return reqSpecifics1;
	}
	public void setReqSpecifics1(String reqSpecifics1) {
		this.reqSpecifics1 = reqSpecifics1;
	}
	public String getReqSpecifics2() {
		return reqSpecifics2;
	}
	public void setReqSpecifics2(String reqSpecifics2) {
		this.reqSpecifics2 = reqSpecifics2;
	}
	public String getSpecifics() {
		return specifics;
	}
	public void setSpecifics(String specifics) {
		this.specifics = specifics;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	@Override
	public String toString() {
		return "Listing [title=" + title + ", fullTitle=" + fullTitle
				+ ", body=" + body + ", price=" + price + ", email=" + email
				+ ", finalized=" + finalized + ", category=" + category
				+ ", categories=" + categories + ", shippingChoice="
				+ shippingChoice + ", shippingOptions=" + shippingOptions.toString()
				+ ", attribute=" + attribute + ", attributes=" + attributes.toString()
				+ ", urls=" + urls.toString() + ", time=" + time + ", condition="
				+ condition + ", buyItNow=" + buyItNow + ", captcha=" + captcha
				+ ", captchaImage=" + captchaImage + ", location=" + location
				+ ", handlingTime=" + handlingTime + ", returns=" + returns
				+ ", reqSpecifics0=" + reqSpecifics0 + ", reqSpecifics1="
				+ reqSpecifics1 + ", reqSpecifics2=" + reqSpecifics2
				+ ", specifics=" + specifics + ", IP=" + IP + "]";
	}


}
