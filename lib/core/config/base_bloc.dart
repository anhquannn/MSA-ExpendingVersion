import 'package:flutter/cupertino.dart';
import 'package:rxdart/rxdart.dart';


class AppContextManager {
  
  static final AppContextManager _instance = AppContextManager._internal();
  
  factory AppContextManager() => _instance;
  
  AppContextManager._internal();
  
  
  final Map<String, BuildContext> _contexts = {};
  
  
  final Map<String, void Function(VoidCallback)> _setStates = {};
  
  
  void registerContext(String key, BuildContext context, void Function(VoidCallback) setState) {
    _contexts[key] = context;
    _setStates[key] = setState;
  }
  
  
  void unregisterContext(String key) {
    _contexts.remove(key);
    _setStates.remove(key);
  }
  
  
  BuildContext? getContext(String key) => _contexts[key];
  
  
  void Function(VoidCallback)? getSetState(String key) => _setStates[key];
}

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
  
  
  String get contextKey => widget.runtimeType.toString();
  
  
  final AppContextManager contextManager = AppContextManager();

  @override
  void initState() {
    onInit();
    super.initState();
    viewContext = context;
    viewSetState = setState;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      contextManager.registerContext(contextKey, context, setState);
    });

    WidgetsBinding.instance.addObserver(this);
    
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
    contextManager.unregisterContext(contextKey);
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


class AppContext {
  static AppContextManager get _manager => AppContextManager();
  
  
  static BuildContext? of(String key) => _manager.getContext(key);
  
  
  static BuildContext? ofType<T>() => _manager.getContext(T.toString());
  
  
  static void Function(VoidCallback)? setStateOf(String key) => _manager.getSetState(key);
  
  
  static void updateUI<T>(VoidCallback fn) {
    final setState = _manager.getSetState(T.toString());
    if (setState != null) {
      setState(fn);
    }
  }
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