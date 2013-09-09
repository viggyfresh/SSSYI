package ebay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import listing.Listing;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.CallRetry;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.AddItemCall;
import com.ebay.sdk.call.GetCategoryFeaturesCall;
import com.ebay.sdk.call.GetCategorySpecificsCall;
import com.ebay.sdk.call.GetProductSearchResultsCall;
import com.ebay.sdk.call.GetSuggestedCategoriesCall;
import com.ebay.sdk.call.ReviseItemCall;
import com.ebay.sdk.call.VerifyAddItemCall;
import com.ebay.sdk.util.eBayUtil;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CharacteristicSetIDsType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeatureDefinitionsType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.GalleryTypeCodeType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingDurationCodeType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.NameRecommendationType;
import com.ebay.soap.eBLBaseComponents.NameValueListArrayType;
import com.ebay.soap.eBLBaseComponents.NameValueListType;
import com.ebay.soap.eBLBaseComponents.PictureDetailsType;
import com.ebay.soap.eBLBaseComponents.ProductListingDetailsType;
import com.ebay.soap.eBLBaseComponents.ProductSearchResultType;
import com.ebay.soap.eBLBaseComponents.ProductSearchType;
import com.ebay.soap.eBLBaseComponents.RecommendationValidationRulesType;
import com.ebay.soap.eBLBaseComponents.RecommendationsType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.SellerPaymentProfileType;
import com.ebay.soap.eBLBaseComponents.SellerProfilesType;
import com.ebay.soap.eBLBaseComponents.SellerReturnProfileType;
import com.ebay.soap.eBLBaseComponents.SellerShippingProfileType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceCodeType;
import com.ebay.soap.eBLBaseComponents.SuggestedCategoryType;
import com.ebay.soap.eBLBaseComponents.ValueRecommendationType;

/* Handles any eBay service calls that the app needs to make.
 * For example, getting suggested categories or listing an item.
 */


public class EbayServiceModule {
	private static ApiContext apiContext;
	private static final Logger log = Logger.getLogger(EbayServiceModule.class.getName());


	public static void init() {
		try {
			apiContext = getApiContext();
		}
		catch (IOException ex) {
			apiContext = null;
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
		}
	}

	// Gets the category specifics of each category in the array of categories that are passed in as input.
	// Returns a JSON object of form { "specific": "blah", "type": "blah", "options": [{"option0": "blah"}] }
	public static ArrayList<String> getCategorySpecifics(String[] categories) throws ApiException, SdkException, Exception {
		ArrayList<String> toReturn = new ArrayList<String>();
		GetCategorySpecificsCall apiCall = new GetCategorySpecificsCall(apiContext);
		apiCall.setCategoryID(categories);
		RecommendationsType[] results = apiCall.getCategorySpecifics();
		for (int index = 0; index < results.length; index++) {
			RecommendationsType result = results[index];
			NameRecommendationType[] recs = result.getNameRecommendation();
			JSONStringer stringer = new JSONStringer();
			stringer.array();
			for (int i = 0; i < recs.length; i++) {
				NameRecommendationType curr = recs[i];
				RecommendationValidationRulesType rule = curr.getValidationRules();
				Integer required = rule.getMinValues();
				if (required != null && required != 0) {
					stringer.object();
					stringer.key("specific");
					stringer.value(curr.getName());
					String inputType = rule.getSelectionMode().value();
					stringer.key("type");
					stringer.value(inputType);
					stringer.key("options");
					stringer.array();
					if (inputType.equals("SelectionOnly")) {
						ValueRecommendationType[] options = curr.getValueRecommendation();
						for (int j = 0; j < options.length; j++) {
							stringer.object();
							stringer.key("option" + j);
							stringer.value(options[j].getValue());
							stringer.endObject();
						}
					}
					stringer.endArray();
					stringer.endObject();
				}
			}
			stringer.endArray();
			toReturn.add(stringer.toString());
		}
		return toReturn;
		//System.out.println(apiCall.getRequestXml());
		//System.out.println(apiCall.getResponseXml());
	}


	// Returns suggested categories for a given query (typically a listing title)
	public static SuggestedCategoryType[] getSuggestedCategories(String query) throws IOException {
		try {
			//Create call object and execute the call
			GetSuggestedCategoriesCall apiCall = new GetSuggestedCategoriesCall(apiContext);
			apiCall.setQuery(query);
			SuggestedCategoryType[] suggestedCategories = apiCall.getSuggestedCategories();
			return suggestedCategories == null ? new SuggestedCategoryType[0] : suggestedCategories;
		} 
		catch(Exception ex) {
			log.severe(ex.toString());
			StackTraceElement[] st = ex.getStackTrace();
			for (int i = 0; i < st.length; i++) {
				log.severe(st[i].toString());
			}
			ex.printStackTrace();
			return new SuggestedCategoryType[0];
		}
	} // main

	// Initializes ApiContext with token and eBay API server URL. STICK TO PROD; SANDBOX WILL BREAK STUFF
	private static ApiContext getApiContext() throws IOException {
		ApiAccount account = new ApiAccount();
		//account.setApplication("eBaye8105-709a-4eb1-9bb5-b24ffe88f46"); // SANDBOX
		account.setApplication("eBaybdb44-525a-45c7-870f-24ed911255a"); // PROD
		//account.setCertificate("c42196d9-4241-4a7a-9007-8cd1b536ede2"); // SANDBOX
		account.setCertificate("0b3c3a9a-9f65-47e4-91d0-70222d75e2e8"); // PROD

		// This is same for prod and sandbox
		account.setDeveloper("3144371a-e03e-4220-a0fe-bf2c3bc205e7");
		ApiCredential credential = new ApiCredential();
		credential.setApiAccount(account);
		//credential.seteBayToken("AgAAAA**AQAAAA**aAAAAA**JPj3UQ**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wFk4GhCpeAoQidj6x9nY+seQ**ykkCAA**AAMAAA**1UtzCxYhN6zNQE7Pn5K8HHFWDXTmOcw3gk8/6otHvxU7lN1w/vZxaSH5zcc/ZtdlaiODT5yyh8uW6cncAP2bxaZ73gSIG7ZXpFxE3PpRNrzmO+62VFJWIjBGUuU8WuuVRqeGpoW5jqAn3jWEWuF+hVgptzRelnwqgs0BdUWNfZOHdbAgkXVqYXY6oEZCbCRzGbyrAosxg8zQKG56cB//Od/eszpWy6K6jaKJXUitaBc+aUytLHN1FpTEmlTkQGH8Pxr2ul0BNBcUP9zyR0tmnEcYkzZfpJyHQ3mo2H9Mj+BFsqQw4+VxeaS3kmwLcumYdKxF6H+DoAbJMpgcOWQGOQKSTGcjgcLm0aFZSpFYTi6raRWeQ8Xe8wqAu4Gz5Xb/XoSqxWwLP8fcjtp7ysk38oDHUojyq39AOHDK9WXZsXuEXje0BRbvjnVo/vCpiaAAtO4mrsyAtgnumBDnHVjIjDlD76xZ9cgobAREU1WkSyY2xBoTfzRg7w1hLxLOnBNByt5qtzrb+9eOPfi9nFK373lIGG5+hQ3rvRDxXZNDICUsORJBl5qQ0+uy9Ab4THbMW04JkAH5Ut4euhW1IwwueWY9rPuJ6jZn+AZyzyZxMibIVj1bcGo9BIG5tKVu9WMXR71nTwH8zFm3muz1a+CEbav+8kJA3CA36FtnrGfhAVhtQ3p6BibHXKnxubBl9IlVsKFfNCy8EVW3sw3yqsN38So1be/uwtXWpjLsLC0wY6U/kx/6mSFMkxIO/wsfDiur"); // SANDBOX
		credential.seteBayToken( "AgAAAA**AQAAAA**aAAAAA**xk8VUg**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AFkoqhDZeFoQydj6x9nY+seQ**0uEBAA**AAMAAA**X2MB/+r4TlLXpZtpCE5Crf88yKmccY4oZi8i8PkUP8D71CVYX3Uf88cXTdqG1mY457/M89PLN/YZNV/GwVJ/vUWqOK/Dik7mCPW9eUN/3q2Xe3tdSonu7EiO9nXouMdCINCh0IkAgUDc6rlZijgs7PhcaWvtK/o8+2/1gGSf9wv6R8xaVzaYr1b+ur7MdAJpESbjy0ZcDBIwnB7Gmtxfptdg1kDQSCiOiekfSq4XV1tPlv8HxiVwMIaCJUePWgK79qDM3OxjaPJgENVtPl4hLteUOUaE/sUMVpCSdIbS4zgErsiW+vsGU8qEWmw1zBWroRkrBs4d6XklAAfcRTHobpsY0bsYHQB+Scqf4t+Nob371zMXv2uoblkJxKb+xtO+pDZJNotNfIOde+EwddfSNizovEfBezQFk0VLvxkO+8BieenENOcJdV8NCYxbqMCWLlN4GyOVnznXkwb+Ab7v+dcBAizT/XXD9dedoFRM3qhN5fMeepK8FGk6f+KqVlrCG2ZG3QZXWgQPLYeMeOg+1XyGrg9HW6D9cdNsaQyAYnTmUtmOCh1bk5nN5jJmBfTgptclvNw3DIv0Q2KMt1cuKPYsPyfOgyaAtcruagTUr8EVFa0YslD3P51ooo9IdyyFfUgTXCjemFs+b06GnpcBqC28vG+7FF0/SAaOYF1aVWWi9sns3u0jmC0OaRiRE7DJ/ksBkVB2UlhX9Iwbn1nUz/gWgC9hXzQ/KkrDIZS8XQAwOXluDu5NVNO2dAb593No" ); // PROD
		ApiContext context = new ApiContext();
		context.setApiCredential(credential);


		// set eBay server URL to call
		//context.setApiServerUrl("https://api.sandbox.ebay.com/wsapi");  // SANDBOX
		context.setApiServerUrl("https://api.ebay.com/wsapi");  // PROD

		// set timeout in milliseconds - 3 minutes
		context.setTimeout(180000);

		// set wsdl version number
		context.setWSDLVersion("613");

		// turn on logging
		//		ApiLogging logging = new ApiLogging();
		//		logging.setLogSOAPMessages(true);
		//		logging.setLogExceptions(true);
		//		context.setApiLogging(logging);

		//context.setEpsServerUrl("https://api.sandbox.ebay.com/ws/api.dll"); // SANDBOX
		context.setEpsServerUrl("https://api.ebay.com/ws/api.dll"); // PROD

		// Set up retry for 3\
		CallRetry cr = new CallRetry();
		cr.setMaximumRetries(3);                      // Max 3 retries
		cr.setDelayTime(1000);          // Retry delay 1000 ms
		cr.setTriggerExceptions(null); // Retry all exceptions
		context.setCallRetry(cr);
		return context;
	} 

	// Revise item call, for when you're modifying an item
	public static String[] reviseItem(Listing l, long count, String EPID) {
		String reviseItemID = l.getFinalized();
		ItemType item = buildItem(l, count, EPID);
		item.setItemID(reviseItemID);
		ReviseItemCall api = new ReviseItemCall(apiContext);
		api.setItemToBeRevised(item);
		FeesType fees = null;
		if (EPID != null) {
			try {
				fees = api.reviseItem();
			}
			catch (Exception ex) {
				log.severe(api.getResponseXml());
				ex.printStackTrace();
				String[] badResult = new String[2];
				badResult[0] = "Error";
				badResult[1] = api.getResponseXml();
				return badResult;
			}
		}
		else {
			try {
				fees = api.reviseItem();
			}
			catch (Exception e) {
				log.info(api.getResponseXml());
				e.printStackTrace();
				EPID = "";
				if (e.getMessage().contains("EPID")) {
					int index = e.getMessage().indexOf("EPID");
					int EPIDindex = index + 5;
					while (e.getMessage().charAt(EPIDindex) != ' ') {
						EPIDindex++;
					}
					EPID = new String(e.getMessage().substring(index + 5, EPIDindex));
					return reviseItem(l, 23, "EPID"+EPID);
				}
				else {
					String[] badResult = new String[2];
					badResult[0] = "Error";
					badResult[1] = api.getResponseXml();
					return badResult;
				}
			}
		}
		double listingFee = eBayUtil.findFeeByName(fees.getFee(), "ListingFee").getFee().getValue();
		String[] returnVal = new String[2];
		returnVal[0] = new Double(listingFee).toString();
		returnVal[1] = item.getItemID();
		return returnVal;
	}






	// Given a listing, proceeds to actually list it on eBay under the account specified in the context
	public static String[] actuallyListItem(Listing l, long count, String EPID) {
		ItemType item = buildItem(l, count, EPID);
		AddItemCall api = new AddItemCall(apiContext);

		// Add pictures
		String base = "C:/Program Files (x86)/Apache Software Foundation/Tomcat 7.0/webapps/PlainTextSYI/files/images/" + count + "_";
		int ticker = 0;
		for (int i = 0; i < 12; i++) {
			if (!l.getUrls().get(i).equals("")) ticker++;
		}
		String[] pictureFiles = new String[ticker];
		for (int i = 0; i < ticker; i++) {
			String url = l.getUrls().get(i);
			int index = url.length() - 1;
			while (url.charAt(index) != '.') {
				index--;
			}
			String suffix = url.substring(index);
			pictureFiles[i] = base + "" + i + "" + suffix;
		}
		api.setPictureFiles(pictureFiles);
		api.setItem(item);
		FeesType fees = null;
		if (EPID != null) {
			try {
				fees = api.addItem();
			}
			catch (Exception ex) {
				log.severe(api.getResponseXml());
				ex.printStackTrace();
				String[] badResult = new String[2];
				badResult[0] = "Error";
				badResult[1] = api.getResponseXml();
				return badResult;
			}
		}
		else {
			try {
				fees = api.addItem();
			}
			catch (Exception e) {
				log.info(api.getResponseXml());
				e.printStackTrace();
				EPID = "";
				if (e.getMessage().contains("EPID")) {
					int index = e.getMessage().indexOf("EPID");
					int EPIDindex = index + 5;
					while (e.getMessage().charAt(EPIDindex) != ' ') {
						EPIDindex++;
					}
					EPID = new String(e.getMessage().substring(index + 5, EPIDindex));
					return actuallyListItem(l, 23, "EPID"+EPID);
				}
				else {
					String[] badResult = new String[2];
					badResult[0] = "Error";
					badResult[1] = api.getResponseXml();
					return badResult;
				}
			}
		}
		double listingFee = eBayUtil.findFeeByName(fees.getFee(), "ListingFee").getFee().getValue();
		String[] returnVal = new String[2];
		returnVal[0] = new Double(listingFee).toString();
		returnVal[1] = item.getItemID();
		return returnVal;
	}

	// Utility function to return the category number from a string of form blah > blah > blah > blah (CATEGORY#)
	public static String getCategoryIDFromCategory(String category) {
		int counter = category.length() - 1;
		while (category.charAt(counter) != '(') {
			counter--;
		}
		String categoryID = category.substring(counter + 1, category.length() - 1);
		return categoryID;
	}

	// Builds the item that will be used in the addItem/reviseItem call
	private static ItemType buildItem(Listing l, long count, String EPID) {
		ItemType item = new ItemType();
		// item title
		item.setTitle(l.getTitle());
		// item description
		String description = l.getBody() + "<br>";
		description += l.getAttribute();
		item.setDescription(description);
		if (EPID != null) {
			ProductListingDetailsType pldt = new ProductListingDetailsType();
			pldt.setProductReferenceID(EPID);
			item.setProductListingDetails(pldt);
		}
		// listing price
		item.setCurrency(CurrencyCodeType.USD);
		AmountType amount = new AmountType();
		amount.setValue(Double.parseDouble(l.getPrice()));
		item.setStartPrice(amount);
		if (!l.getBuyItNow().equals("")) {
			AmountType bin = new AmountType();
			bin.setValue(Double.parseDouble(l.getBuyItNow()));
			item.setBuyItNowPrice(bin);
		}
		// listing duration
		if (l.getTime().equals("3")) item.setListingDuration(ListingDurationCodeType.DAYS_3.value());
		else if (l.getTime().equals("5")) item.setListingDuration(ListingDurationCodeType.DAYS_5.value());
		else if (l.getTime().equals("7")) item.setListingDuration(ListingDurationCodeType.DAYS_7.value());

		// item location and country
		if (l.getLocation().equals("") || l.getLocation() == null) item.setLocation("USA");
		else item.setLocation(l.getLocation());
		item.setCountry(CountryCodeType.US);

		JSONArray specJSON = new JSONArray(l.getSpecifics());
		int specCount = specJSON.length();
		if (specCount != 0) {
			NameValueListType[] specifics = new NameValueListType[specCount];
			for (int i = 0; i < specCount; i++) {
				JSONObject curr = specJSON.getJSONObject(i);
				String key = curr.getString("key");
				String value = curr.getString("value");
				NameValueListType temp = new NameValueListType();
				temp.setName(key);
				temp.setValue(new String[]{ value });
				specifics[i] = temp;
			}
			NameValueListArrayType nv = new NameValueListArrayType();
			nv.setNameValueList(specifics);
			item.setItemSpecifics(nv);
		}

		// listing category
		CategoryType cat = new CategoryType();
		String category = l.getCategory();
		int counter = category.length() - 1;
		while (category.charAt(counter) != '(') {
			counter--;
		}
		String categoryID = category.substring(counter + 1, category.length() - 1);
		cat.setCategoryID(categoryID);
		item.setPrimaryCategory(cat);

		// item quantity
		item.setQuantity(new Integer(1));

		// item condition		
		if (l.getCondition().equals("New")) item.setConditionID(1000);
		else if (l.getCondition().equals("Used")) item.setConditionID(3000);
		else if (l.getCondition().equals("New other")) item.setConditionID(1500);
		else if (l.getCondition().equals("Manufacturer refurbished")) item.setConditionID(2000);
		else if (l.getCondition().equals("Seller refurbished")) item.setConditionID(2500);
		else if (l.getCondition().equals("For parts or not working")) item.setConditionID(7000);

		// payment - currently faking it =D
		BuyerPaymentMethodCodeType[] arrPaymentMethods = new BuyerPaymentMethodCodeType[]{BuyerPaymentMethodCodeType.PAY_PAL};
		item.setPayPalEmailAddress("test@pp.com");
		item.setPaymentMethods(arrPaymentMethods);

		item.setShippingDetails(getShippingDetails(l));

		item.setDispatchTimeMax(3);
		item.setReturnPolicy(getReturnPolicy(l));

		PictureDetailsType pdt = new PictureDetailsType();
		pdt.setGalleryType(GalleryTypeCodeType.GALLERY);
		item.setPictureDetails(pdt);
		return item;
	}

	// Sets up return policy
	private static ReturnPolicyType getReturnPolicy(Listing l) {
		if (l.getReturns().equalsIgnoreCase("yes")) {
			ReturnPolicyType rp = new ReturnPolicyType();
			rp.setDescription("Returns accepted.");
			rp.setReturnsAcceptedOption("ReturnsAccepted");
			rp.setReturnsWithinOption("Days_14");
			return rp;
		}
		else {
			ReturnPolicyType rp = new ReturnPolicyType();
			rp.setDescription("Returns not accepted.");
			rp.setReturnsAcceptedOption("ReturnsNotAccepted");
			return rp;
		}
	}

	// Sets up shipping options
	private static ShippingDetailsType getShippingDetails(Listing l) {
		ShippingDetailsType sd = new ShippingDetailsType();
		String shippingChoice = l.getShippingChoice();
		if (shippingChoice.startsWith("Free Shipping")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setFreeShipping(true);
			st1.setShippingService(ShippingServiceCodeType.USPS_PRIORITY.value());
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
			return sd;
		}
		else if (shippingChoice.startsWith("No shipping")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setLocalPickup(true);
			st1.setFreeShipping(true);
			st1.setShippingService(ShippingServiceCodeType.PICKUP.value());
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
			return sd;
		}
		int priceIndex = shippingChoice.length() - 1;
		while (shippingChoice.charAt(priceIndex) != '$') priceIndex--;
		String costString = shippingChoice.substring(priceIndex + 1, shippingChoice.length() - 1);
		double cost = Double.parseDouble(costString);
		if (shippingChoice.startsWith("US Postal Service Priority Mail Flat Rate Envelope")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_PRIORITY_MAIL_FLAT_RATE_ENVELOPE.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service First Class Package")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_FIRST_CLASS.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service Media Mail")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_MEDIA.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service Priority Mail Small Flat Rate Box")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_PRIORITY_MAIL_SMALL_FLAT_RATE_BOX.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service Priority Mail Medium Flat Rate Box")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_PRIORITY_FLAT_RATE_BOX.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service Priority Mail Large Flat Rate Box")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_PRIORITY_MAIL_LARGE_FLAT_RATE_BOX.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service Parcel Post")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_PARCEL.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		else if (shippingChoice.startsWith("US Postal Service Priority Mail")) {
			ShippingServiceOptionsType st1 = new ShippingServiceOptionsType();
			st1.setShippingService(ShippingServiceCodeType.USPS_PRIORITY.value());
			st1.setShippingServiceCost(getAmount(cost));
			sd.setShippingServiceOptions(new ShippingServiceOptionsType[]{st1});
		}
		return sd;
	}

	// Utility function to declare an AmountType with a specific amount
	private static AmountType getAmount(double amount) {
		AmountType a = new AmountType();
		a.setValue(amount);
		return a;
	}

	// Test functions here =)
	public static void main(String[] args) throws ApiException, SdkException, Exception {
		init();
		//		SuggestedCategoryType[] categories = getSuggestedCategories("iphone 5 64gb");
		//		for (SuggestedCategoryType curr : categories) {
		//			System.out.println(curr.getCategory().getCategoryName());
		//		}

		//		String query = "apple macbook pro 15\" retina display intel 2.4 ghz core i7 quad 16gb ram";
		//		String category = "111422";
		//		getEPID(query, category);

		//		Listing l = new Listing();
		//		l.setTitle("selling iphone 4s 16gb verizon white");
		//		l.setBody("NOTHING IS ACTUALLY FOR SALE - BID AT YOUR OWN PERIL");
		//		l.setCategory("Cell Phones and Smartphones (9355)");
		//		l.setCondition("New");
		//		l.setTime("5");
		//		l.setPrice("1999.00");
		//		ArrayList<String> u = new ArrayList<String>();
		//		u.add("poop.png");
		//		for (int i = 0; i < 11; i++) {
		//			u.add("");
		//		}
		//		l.setUrls(u);
		//		l.setShippingChoice("Free Shipping");
		//		try {
		//			String[] results = actuallyListItem(l, 23, null);
		//			System.out.println("Listing fees: " + results[0]);
		//			System.out.println("Listing id: " + results[1]);
		//		} catch (Exception e) {
		//			
		//		}

		//		String categoryID = "9355"; // Cell phones and smartphones
		String categoryID = "11483"; // Men's pants =)
		String categoryID1 = "15709"; // Men's shoes =)

		//		String categoryID = "11116"; // Coins and Paper Money
		//		String categoryID = "69323"; // Film photography
		//		String categoryID = "171961"; // Printer stuff
		//		String categoryID = "176984"; //CDs
		//		String categoryID = "93427";
		String[] categories = new String[2];
		categories[0] = categoryID;
		categories[1] = categoryID1;

		for (int blah = 0; blah < 2; blah++) {
			String specificsJSON = getCategorySpecifics(categories).get(blah);
			System.out.println(specificsJSON);
			JSONArray j = new JSONArray(specificsJSON);
			for (int i = 0; i < j.length(); i++) {
				JSONObject curr = j.getJSONObject(i);
				System.out.println("Specific: " + curr.get("specific"));
				System.out.println("Type: " + curr.get("type"));
				JSONArray options = curr.getJSONArray("options");
				for (int k = 0; k < options.length(); k++) {
					JSONObject subcurr = options.getJSONObject(k);
					System.out.println("Option " + k + ": " + subcurr.get("option" + k));
				}
			}
			System.out.println("--------------------");
		}
	}
}