// import 'package:flutter/cupertino.dart';
// import 'package:rxdart/rxdart.dart';
//
// abstract class BaseView extends StatefulWidget {
//   BaseView({Key? key, this.context, this.setState}) : super(key: key);
//
//   late final BuildContext? context;
//   late final Function(VoidCallback)? setState;
//
//   @protected
//   Widget build(BuildContext context);
// }
//
// abstract class BaseBloc<S extends BaseView> extends State<S>
//     with WidgetsBindingObserver {
//   @override
//   void initState() {
//     super.initState();
//     if (widget.context == null) {
//       widget.context = context;
//     }
//     if (widget.setState == null) {
//       widget.setState = setState;
//     }
//
//     WidgetsBinding.instance.addObserver(this);
//     onInit();
//
//     WidgetsBinding.instance.addPostFrameCallback((_) => onReady());
//   }
//
//   @override
//   void didChangeAppLifecycleState(AppLifecycleState state) {
//     super.didChangeAppLifecycleState(state);
//     if (state == AppLifecycleState.resumed) {
//       onResumed();
//     }
//   }
//
//   @override
//   void dispose() {
//     super.dispose();
//     onDispose();
//     WidgetsBinding.instance.removeObserver(this);
//   }
//
//   @protected
//   void onInit();
//
//   @protected
//   void onReady();
//
//   @protected
//   void onResumed();
//
//   @protected
//   void onDispose();
//
//   @override
//   Widget build(BuildContext context) => widget.build(context);
// }
//
// extension BehaviorSubjectExtension<T> on BehaviorSubject<T> {
//   void set(T event, {Function? function}) {
//     function?.call();
//     if (!this.isClosed) {
//       sink.add(event);
//     }
//   }
//
//   void setError(String event, {Function? function}) {
//     function?.call();
//     // Kiểm tra xem BehaviorSubject có đóng hay không trước khi phát lỗi
//     if (!isClosed) {
//       sink.addError(event);
//     }
//   }
//
//   ValueStream<T> get output => this.stream;
// }
import 'package:flutter/cupertino.dart';
import 'package:rxdart/rxdart.dart';

abstract class BaseView<T extends BaseBloc> extends StatefulWidget {
  const BaseView({super.key});

  T createBloc();

  @override
  State<StatefulWidget> createState() => createBloc();
}

abstract class BaseBloc<S extends StatefulWidget> extends State<S>
    with WidgetsBindingObserver {
  late BuildContext viewContext;
  late void Function(VoidCallback) viewSetState;

  @override
  void initState() {
    super.initState();
    viewContext = context;
    viewSetState = setState;

    WidgetsBinding.instance.addObserver(this);
    onInit();
    WidgetsBinding.instance.addPostFrameCallback((_) => onReady());
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    if (state == AppLifecycleState.resumed) {
      onResumed();
    }
  }

  @override
  void dispose() {
    onDispose();
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @protected
  void onInit();

  @protected
  void onReady();

  @protected
  void onResumed();

  @protected
  void onDispose();
}

extension BehaviorSubjectExtension<T> on BehaviorSubject<T> {
  void set(T event, {Function? function}) {
    function?.call();
    if (!isClosed) sink.add(event);
  }

  void setError(String event, {Function? function}) {
    function?.call();
    if (!isClosed) sink.addError(event);
  }

  ValueStream<T> get output => stream;
}
