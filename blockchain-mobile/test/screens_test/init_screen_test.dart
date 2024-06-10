import 'package:flutter_test/flutter_test.dart';

void main() {
  group("InitScreen", () {
    testWidgets(
        "test BottomNavigationBar Switch UI on tap", _testBottomNavBarSwitchUI);
  });
}

Future<void> _testBottomNavBarSwitchUI(WidgetTester tester) async {
  // TestWidgetsFlutterBinding.ensureInitialized();
  // SharedPreferences.setMockInitialValues({}); //set values here
  // SharedPreferences pref = await SharedPreferences.getInstance();
  // // Prepare widget
  // await tester.runAsync(() async {
  //   await tester.pumpWidget(
  //       const MaterialApp(home: InitScreen()), const Duration(seconds: 1));
  // });

  // // Verify that the InitScreen is on the first element
  // var indicator = find.byType(CircularProgressIndicator);
  // expect(indicator, findsOneWidget, reason: "Init Screen need to load");
  // // await tester.runAsync(() async {
  // //   while (!find.byType(HomeScreen).hasFound) {
  // await tester.pumpAndSettle();
  // //   }
  // // });
  // expect(find.byType(HomeScreen), findsOneWidget);
  // expect(find.byType(FavoriteScreen), findsNothing);

  // // Simulate a tap on a widget inside InitScreen

  // var favoriteIcon = find.byWidgetPredicate((widget) {
  //   return widget is SvgPicture &&
  //       widget.bytesLoader.toString().contains("Heart Icon");
  // }).last;
  // await tester.tap(favoriteIcon);
  // await tester.pump();

  // expect(find.byType(HomeScreen), findsNothing);
  // expect(find.byType(FavoriteScreen), findsOneWidget);
}
