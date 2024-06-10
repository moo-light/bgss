// import 'package:blockchain_mobile/1_controllers/services/auth_service.dart';
// import 'package:either_dart/either.dart';
// import 'package:flutter_dotenv/flutter_dotenv.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:mockito/annotations.dart';
// import 'package:mockito/mockito.dart' as mockito;
// import 'package:shared_preferences/shared_preferences.dart';

// import '../mocks/auth_repository_mock.dart';
// @GenerateMocks([AuthRepositoryMock])
// import '../mocks/auth_repository_mock.mocks.dart';
// import 'auth_service_test.mocks.dart';

// void main() {
//   group("Auth Service: ", () {
//     group("user sign in", userSignInGroup);
//     group("user sign up", userSignUpGroup);
//     group("user sign out", userSignOutGroup);
//   });
// }

// void userSignInGroup() {
//   setUp(() async {
//     mockito.provideDummy<Either<dynamic, String?>>(const Left('Success'));
//     mockito.provideDummy<Either<dynamic, String?>>(const Right('Error'));

//     SharedPreferences.setMockInitialValues({});
//     await dotenv.load();
//   });
//   tearDown(() async {});
//   test(
//     "should return Success",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();
//       var authRepositoryMock = MockAuthRepositoryMock();
//       var username = "user";
//       var password = "123456";
//       mockito
//           .when(authRepositoryMock.login(username, password))
//           .thenAnswer((_) async {
//         return (const Left('Success'));
//       });

//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignIn(username, password);

//       //Assert
//       expect(result.isLeft, true);
//     },
//   );
//   test(
//     "should return Error",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();
//       var authRepositoryMock = MockAuthRepositoryMock();
//       var username = "user";
//       var password = "123456";
//       //Mock responses
//       const errorField = 'Error';
//       mockito
//           .when(authRepositoryMock.login(username, password))
//           .thenAnswer((_) async {
//         return (const Right(errorField));
//       });
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignIn(username, password);

//       //Assert
//       expect(result.right, errorField);
//     },
//   );
// }

// void userSignUpGroup() {
//   setUp(() async {
//     mockito.provideDummy<Either<dynamic, String?>>(const Left('Success'));
//     mockito.provideDummy<Either<String, String?>>(const Right('Error'));

//     SharedPreferences.setMockInitialValues({});
//     await dotenv.load();
//   });
//   tearDown(() async {});
//   var username = "user";
//   var email = "user@gmail.com";
//   var password = "123456";
//   String? firstName = "testing";
//   String? lastName = "testing";
//   String? phone = "any phone number";
//   test(
//     "should return Success",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();
//       var authRepositoryMock = MockAuthRepositoryMock();

//       mockito
//           .when(authRepositoryMock.login(username, password))
//           .thenAnswer((_) async {
//         return (const Left('Success'));
//       });
//       mockito
//           .when(authRepositoryMock.register(
//               username, email, password, firstName, lastName, phone))
//           .thenAnswer((_) async {
//         return (const Left('Success'));
//       });
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignUp(
//           username, email, password, firstName, lastName, phone);

//       //Assert
//       expect(result.isLeft, true);
//     },
//   );
//   test(
//     "should return Error",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();
//       var authRepositoryMock = MockAuthRepositoryMock();
//       var username = "user";
//       var email = "user@gmail.com";
//       var password = "123456";
//       //Mock responses
//       const errorField = 'Error';
//       mockito
//           .when(authRepositoryMock.login(username, password))
//           .thenAnswer((_) async {
//         return (const Right(errorField));
//       });
//       mockito
//           .when(authRepositoryMock.register(
//               username, email, password, firstName, lastName, phone))
//           .thenAnswer((_) async {
//         return (const Right('Error'));
//       });
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignUp(
//           username, email, password, firstName, lastName, phone);

//       //Assert
//       expect(result.isLeft, false);
//       expect(result.right, errorField);
//     },
//   );
//   test(
//     "should throw Unhandled Error",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();
//       var authRepositoryMock = MockAuthRepositoryMock();
//       var username = "user";
//       var email = "user@gmail.com";
//       var password = "123456";
//       //Mock responses
//       const errorField = 'Error';
//       mockito
//           .when(authRepositoryMock.login(username, password))
//           .thenAnswer((_) async {
//         return (const Right(errorField));
//       });
//       mockito
//           .when(authRepositoryMock.register(
//               username, email, password, firstName, lastName, phone))
//           .thenAnswer((_) async {
//         return (const Left('Success'));
//       });
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       result() async => await authService.userSignUp(
//           username, email, password, firstName, lastName, phone);

//       //Assert
//       result().onError((Exception error, stackTrace) {
//         // expect(error.message, );
//         expect(error.toString(), contains("automatically login failed"));

//         return const Right(errorField);
//       });
//     },
//   );
// }

// void userSignOutGroup() {
//   MockAuthRepositoryMock authRepositoryMock = MockAuthRepositoryMock();

//   setUp(() async {
//     mockito.provideDummy<Either<String, String?>>(const Left('Success'));
//     mockito.provideDummy<Either<bool, String?>>(const Left(true));
//     mockito.when(authRepositoryMock.refreshToken()).thenAnswer((_) async {
//       return (const Left('Success'));
//     });
//     SharedPreferences.setMockInitialValues({});

//     await dotenv.load();
//   });
//   tearDown(() async {});
//   test(
//     "should return Logout Success",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();

//       mockito.when(authRepositoryMock.logout()).thenAnswer((_) async {
//         return (const Left(true));
//       });
//       //Assert before act
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignOut();

//       //Assert
//       expect(result.isLeft, true);
//       mockito.verify(authRepositoryMock.refreshToken()).called(1);
//       mockito.verify(authRepositoryMock.logout()).called(1);
//     },
//   );
//   test(
//     "should return Logout Fail",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();

//       //Mock responses
//       mockito.when(authRepositoryMock.logout()).thenAnswer((_) async {
//         return (const Left(false));
//       });

//       //Assert before act
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignOut();
//       //Assert
//       expect(result.isLeft, true);
//       expect(result.left, false);
//     },
//   );
//   test(
//     "should return Error",
//     () async {
//       //Arrange
//       var pref = await SharedPreferences.getInstance();
//       //Mock responses
//       const errorField = 'Error'; // actually any value is possible
//       mockito.when(authRepositoryMock.refreshToken()).thenAnswer((_) async {
//         return (const Left('Success'));
//       });
//       mockito.when(authRepositoryMock.logout()).thenAnswer((_) async {
//         return (const Right(errorField));
//       });

//       //Assert before act
//       //Act
//       var authService = AuthService(authRepository: authRepositoryMock);
//       var result = await authService.userSignOut();
//       //Assert
//       expect(result.isLeft, false);
//       expect(result.right, contains(errorField));
//     },
//   );
// }
