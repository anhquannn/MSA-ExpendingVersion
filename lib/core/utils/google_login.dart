import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';

Future<UserCredential?> signInWithGoogle() async {
  try {
    // B1: Tạo đối tượng GoogleSignIn
    final GoogleSignIn googleSignIn = GoogleSignIn.standard();

    // B2: Cho người dùng chọn tài khoản Google
    final GoogleSignInAccount? googleUser = await googleSignIn.signIn();

    if (googleUser == null) {
      print('❌ Người dùng đã hủy đăng nhập');
      return null;
    }

    // B3: Lấy token
    final GoogleSignInAuthentication googleAuth = await googleUser.authentication;

    // B4: Tạo credential để đăng nhập Firebase
    final OAuthCredential credential = GoogleAuthProvider.credential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );

    // B5: Đăng nhập Firebase
    final userCredential = await FirebaseAuth.instance.signInWithCredential(credential);
    print('✅ Đăng nhập thành công: ${userCredential.user?.email}');
    return userCredential;
  } catch (e, stack) {
    print('❌ Lỗi đăng nhập Google: $e');
    print('📛 Stacktrace: $stack');
    return null;
  }
}
