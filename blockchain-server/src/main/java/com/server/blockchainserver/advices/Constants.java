package com.server.blockchainserver.advices;

public class Constants {

    public static final int OTP_LENGTH = 6;

    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_STAFF = "ROLE_STAFF";
    public static final String PREAUTHORIZE_ALL_ROLE= "hasRole('ROLE_CUSTOMER') or hasRole('ROLE_STAFF') or hasRole('ROLE_ADMIN')";
    public static final String Insufficient_Balance = "Insufficient balance";

    public static final String Cancel_Transaction = "Cancel transaction";

    public static final String Successfully_Transaction = "Successfully Transaction";

    public static final String Not_Registered_InternetBanking = "Customer's card/account has not registered for InternetBanking service at the bank";

    public static final String Verify_Incorrect = "Customers verify incorrect card/account information more than 3 times";

}
