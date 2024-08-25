package com.couriermanagement.utility;

public class Constants {
	
	public enum UserRole {
		ROLE_COURIER("Courier"), ROLE_ADMIN("Admin"), ROLE_CUSTOMER("Customer"), ROLE_DELIVERY("Delivery");

		private String role;

		private UserRole(String role) {
			this.role = role;
		}

		public String value() {
			return this.role;
		}
	}
	
	public enum ActiveStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		private ActiveStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}
	
	public enum DeliveryStatus {
		DELIVERED("Delivered"),
	    ON_THE_WAY("On the Way"),
	    PENDING("Pending"),    // If admin doesn't take any action
	    PROCESSING("Processing"),
	    OUT_FOR_DELIVERY("Out for Delivery"),
	    AT_DESTINATION_HUB("At Destination Hub"),
	    EXCEPTION("Exception"),  // For exceptional cases (e.g., address issue, weather delay)
	    RETURNED("Returned"),
	    CANCELED("Canceled");
		
		
		private String status;

	    private DeliveryStatus(String status) {
	      this.status = status;
	    }

	    public String value() {
	      return this.status;
	    }
	     
	}
	
	public enum DeliveryTime {
		MORNING("Morning"),
		AFTERNOON("Afternoon"),
		EVENING("Evening"),  
		NIGHT("Night");	
		
		
		private String time;

	    private DeliveryTime(String time) {
	      this.time = time;
	    }

	    public String value() {
	      return this.time;
	    }
	     
	}
	
	public enum Category {
		
		DOCUMENTS("Documents"),
		ELECTRONICS("Electronics"),
		FURNITURE("Furniture"),  
		EATABLES("Eatables"),
		JEWELLERY("Jewellery"),
		LUGGAGES("Luggages");	
		
		
		private String category;

	    private Category(String category) {
	      this.category = category;
	    }

	    public String value() {
	      return this.category;
	    }
	}

}
