//package com.server.blockchainserver.platform.hyperledgerfabric.network;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonParser;
//import com.server.blockchainserver.platform.entity.enums.ContractStatus;
//import com.server.blockchainserver.platform.entity.enums.GoldUnit;
//import com.server.blockchainserver.platform.entity.enums.TransactionType;
//import io.grpc.Grpc;
//import io.grpc.ManagedChannel;
//import io.grpc.TlsChannelCredentials;
//import jakarta.annotation.PostConstruct;
//import jakarta.transaction.Transactional;
//import org.hyperledger.fabric.client.*;
//import org.hyperledger.fabric.client.identity.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.InvalidKeyException;
//import java.security.cert.CertificateException;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@PropertySource("classpath:application.properties")
//public class FabricGateway {
//
//    @Value("${fabric.org.msp}")
//    private static final String orgMSP = "Org1MSP";
//
//    @Value("${fabric.crypto.path}")
//    private static String cryptoPath = "/root/server-deploy/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com";
//
//    @Value("${fabric.cert.dir.path}")
//    private static String certDirPath = "/root/server-deploy/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts";
//
//    @Value("${fabric.key.dir.path}")
//    private static String keyDirPath = "/root/server-deploy/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore";
//
//    @Value("${fabric.tls.cert.path}")
//    private static String tlsCertPath = "/root/server-deploy/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt";
//
//    @Value("${fabric.channel.name}")
//    private static String channelName = "mychannel";
//
//    @Value("${fabric.chaincode.name}")
//    private static String chaincodeName = "basic";
//
//    @Value("${fabric.peer.endpoint}")
//    private static String PEER_ENDPOINT = "localhost:7051";
//
//    @Value("${fabric.override_auth}")
//    private static String OVERRIDE_AUTH = "peer0.org1.example.com";
//
//    @Value("${fabric.asset.param}")
//    private static String assetID = "asset";
//
//
//    private static final String MSP_ID = System.getenv().getOrDefault("MSP_ID", orgMSP);
//    //Path to crypto materials.
//    private static final Path CRYPTO_PATH = Paths.get(cryptoPath);
//    // Path to user certificate.
//    private static final Path CERT_DIR_PATH = CRYPTO_PATH.resolve(Paths.get(certDirPath));
//    // Path to user private key directory.
//    private static final Path KEY_DIR_PATH = CRYPTO_PATH.resolve(Paths.get(keyDirPath));
//    // Path to peer tls certificate.
//    private static final Path TLS_CERT_PATH = CRYPTO_PATH.resolve(Paths.get(tlsCertPath));
//    // Gateway peer end point.
////    private static final String PEER_ENDPOINT = "localhost:7051";
////    private static final String OVERRIDE_AUTH = "peer0.org1.example.com";
//
//    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", channelName);
//
//    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", chaincodeName);
//
//    private final Contract contract;
//
//    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//    public FabricGateway(final Gateway gateway) {
//        var network = gateway.getNetwork(CHANNEL_NAME);
//        contract = network.getContract(CHAINCODE_NAME);
//    }
//
//
//    private Gateway connectToGateway() throws Exception {
//        var builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(newGrpcConnection())
//                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
//                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
//                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
//                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));
//        return builder.connect();
//
//    }
//
//    /**
//     * This type of transaction would typically only be run once by an application
//     * the first time it was started after its initial deployment. A new version of
//     * the chaincode deployed later would likely not need to run an "init" function.
//     */
//    public byte[] initLedger() throws Exception {
//
//        var builder = connectToGateway();
//        var channel = newGrpcConnection();
//        try (builder) {
//            System.out.println("\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger");
//
//            var result = new FabricGateway(builder).contract.submitTransaction("InitLedger");
//
//            System.out.println("*** Transaction committed successfully");
//
//            return result;
//        } finally {
//            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
//        }
//    }
//
//    /**
//     * Evaluate a transaction to query ledger state.
//     */
//    public String getAllAssets() throws Exception {
//
//        var builder = connectToGateway();
//        var channel = newGrpcConnection();
//        try (builder) {
//            System.out.println("\n--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger");
//
//            var result = new FabricGateway(builder).contract.evaluateTransaction("GetAllAssets");
//
//            System.out.println("*** Result: " + prettyJson(result));
//
//            return prettyJson(result);
//        } finally {
//            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
//        }
//    }
//
//    private String prettyJson(final byte[] json) {
//        return prettyJson(new String(json, StandardCharsets.UTF_8));
//    }
//
//    private String prettyJson(final String json) {
//        var parsedJson = JsonParser.parseString(json);
//        return gson.toJson(parsedJson);
//    }
//
//    /**
//     * Submit a transaction synchronously, blocking until it has been committed to
//     * the ledger.
//     */
//    @Transactional
//    public void createAsset(Long contractId, Long actionParty, String fullName, String address, BigDecimal quantity,
//                            BigDecimal pricePerOunce, BigDecimal totalCostOrProfit, TransactionType transactionType,
//                            Date createdAt, String confirmingParty, GoldUnit goldUnit, String signatureActionParty,
//                            String signatureConfirmingParty, ContractStatus contractStatus) throws EndorseException, SubmitException, CommitStatusException, CommitException {
//        System.out.println("\n--> Submit Transaction: CreateAsset, creates new asset with ID, Color, Size, Owner and AppraisedValue arguments");
//        // Convert all arguments to String
//        System.out.println(createdAt.getTime());
//        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_INSTANT;
//        String createdAtIso = isoFormatter.format(ZonedDateTime.ofInstant(createdAt.toInstant(), ZoneId.of("UTC")));
//
//        // Call submitTransaction with the function name and the arguments
//        contract.submitTransaction("createAsset", assetID + contractId,
//                contractId.toString(), actionParty.toString(), fullName, address, quantity.toString(),
//                pricePerOunce.toString(), totalCostOrProfit.toString(), transactionType.name(), createdAtIso,
//                confirmingParty, goldUnit.name(), signatureActionParty, signatureConfirmingParty, contractStatus.name());
//        System.out.println("*** Transaction committed successfully");
//    }
//
//
//    /**
//     * Submit transaction asynchronously, allowing the application to process the
//     * smart contract response (e.g. update a UI) while waiting for the commit
//     * notification.
//     */
////    private void transferAssetAsync() throws EndorseException, SubmitException, CommitStatusException {
////        System.out.println("\n--> Async Submit Transaction: TransferAsset, updates existing asset owner");
////
////        var commit = contract.newProposal("TransferAsset")
////                .addArguments(assetId, "Saptha")
////                .build()
////                .endorse()
////                .submitAsync();
////
////        var result = commit.getResult();
////        var oldOwner = new String(result, StandardCharsets.UTF_8);
////
////        System.out.println("*** Successfully submitted transaction to transfer ownership from " + oldOwner + " to Saptha");
////        System.out.println("*** Waiting for transaction commit");
////
////        var status = commit.getStatus();
////        if (!status.isSuccessful()) {
////            throw new RuntimeException("Transaction " + status.getTransactionId() +
////                    " failed to commit with status code " + status.getCode());
////        }
////
////        System.out.println("*** Transaction committed successfully");
////    }
//
////    private void readAssetById() throws GatewayException {
////        System.out.println("\n--> Evaluate Transaction: ReadAsset, function returns asset attributes");
////
////        var evaluateResult = contract.evaluateTransaction("ReadAsset", assetId);
////
////        System.out.println("*** Result:" + prettyJson(evaluateResult));
////    }
//
//    /**
//     * submitTransaction() will throw an error containing details of any error
//     * responses from the smart contract.
//     */
////    private void updateNonExistentAsset() {
////        try {
////            System.out.println("\n--> Submit Transaction: UpdateAsset asset70, asset70 does not exist and should return an error");
////
////            contract.submitTransaction("UpdateAsset", "asset70", "blue", "5", "Tomoko", "300");
////
////            System.out.println("******** FAILED to return an error");
////        } catch (EndorseException | SubmitException | CommitStatusException e) {
////            System.out.println("*** Successfully caught the error: ");
////            e.printStackTrace(System.out);
////            System.out.println("Transaction ID: " + e.getTransactionId());
////
////            var details = e.getDetails();
////            if (!details.isEmpty()) {
////                System.out.println("Error Details:");
////                for (var detail : details) {
////                    System.out.println("- address: " + detail.getAddress() + ", mspId: " + detail.getMspId()
////                            + ", message: " + detail.getMessage());
////                }
////            }
////        } catch (CommitException e) {
////            System.out.println("*** Successfully caught the error: " + e);
////            e.printStackTrace(System.out);
////            System.out.println("Transaction ID: " + e.getTransactionId());
////            System.out.println("Status code: " + e.getCode());
////        }
////    }
//    public static ManagedChannel newGrpcConnection() throws IOException {
//        var credentials = TlsChannelCredentials.newBuilder()
//                .trustManager(TLS_CERT_PATH.toFile())
//                .build();
//        return Grpc.newChannelBuilder(PEER_ENDPOINT, credentials)
//                .overrideAuthority(OVERRIDE_AUTH)
//                .build();
//    }
//
//    public static Identity newIdentity() throws IOException, CertificateException {
//        try (var certReader = Files.newBufferedReader(getFirstFilePath(CERT_DIR_PATH))) {
//            var certificate = Identities.readX509Certificate(certReader);
//            return new X509Identity(MSP_ID, certificate);
//        }
//    }
//
//    public static Signer newSigner() throws IOException, InvalidKeyException {
//        try (var keyReader = Files.newBufferedReader(getFirstFilePath(KEY_DIR_PATH))) {
//            var privateKey = Identities.readPrivateKey(keyReader);
//            return Signers.newPrivateKeySigner(privateKey);
//        }
//    }
//
//    public static Path getFirstFilePath(Path dirPath) throws IOException {
//        try (var keyFiles = Files.list(dirPath)) {
//            return keyFiles.findFirst().orElseThrow();
//        }
//    }
//}
