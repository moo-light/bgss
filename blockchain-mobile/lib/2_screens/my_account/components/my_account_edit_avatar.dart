import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/2_screens/my_account/components/profile_pic.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class MyAccountEditAvatar extends StatelessWidget {
  final User currentUser;

  const MyAccountEditAvatar({super.key, required this.currentUser});

  @override
  Widget build(BuildContext context) {
    final croppedFile = context.watch<AppImageProvider>().croppedFile;
    return Column(
      children: [
        Text("Avatar", style: Theme.of(context).textTheme.headlineMedium),
        const Gap(20),
        const ProfileScreenPic(),
        const Gap(10),
        Visibility(
          visible: croppedFile != null,
          child: IsLoadingButton(
            isLoading: context.watch<AuthProvider>().isAvatarSubmitting,
            child: ElevatedButton(
              style: (Theme.of(context).elevatedButtonTheme.style)!.copyWith(
                  backgroundColor:
                      const MaterialStatePropertyAll(kSecondaryColor),
                  shadowColor: const MaterialStatePropertyAll(Colors.grey)),
              // Submit
              onPressed: () async {
                if (croppedFile != null) {
                  final isUpdated = await context
                      .read<AuthProvider>()
                      .updateUserAvatar(context, avatar: croppedFile);
                  if (isUpdated && context.mounted) {
                    context.read<AppImageProvider>().clearImage();
                  }
                }
              },
              child: Text(
                "Save Changes",
                style: Theme.of(context)
                    .textTheme
                    .bodyMedium!
                    .copyWith(color: Colors.black),
              ),
            ),
          ),
        ),
        const Gap(10),
        Text(
          currentUser.displayName,
          style: Theme.of(context).textTheme.headlineMedium!.copyWith(
                fontWeight: FontWeight.bold,
                fontSize: 24,
              ),
          textAlign: TextAlign.center,
        ),
        const Gap(5),
        Text(
          currentUser.email,
          style: Theme.of(context).textTheme.headlineSmall!.copyWith(
                color: kHintTextColor,
                fontSize: 20  ,
              ),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }
}
