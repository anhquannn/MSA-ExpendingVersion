import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart' as Storage;
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/model/request/feedback_filter_request_model.dart';
import 'package:msa/feature/data/model/response/feedback_filter_response.dart'
    show FeedbacFilterkResponse;
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import 'package:rxdart/rxdart.dart';

class RatingScreen extends StatefulWidget {
  final int? productId;
  const RatingScreen({super.key, this.productId});

  @override
  State<RatingScreen> createState() => _RatingScreenState();
}

class _RatingScreenState extends State<RatingScreen> {
  final List<FeedbacFilterkResponse> listFeedback = [];
  final streamListFeedbak = BehaviorSubject<List<FeedbacFilterkResponse>>();

  final ScrollController _scrollController = ScrollController();

  int _page = 1;
  bool _isLoading = false;
  bool _hasMore = true;

  int _selectedRating = 0;

  @override
  void initState() {
    super.initState();
    onGetFeedBack();

    _scrollController.addListener(() {
      if (_scrollController.position.pixels >=
          _scrollController.position.maxScrollExtent - 100) {
        onGetFeedBack();
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    streamListFeedbak.close();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
            _scrollController.position.maxScrollExtent - 200 &&
        !_isLoading &&
        _hasMore) {
      onGetFeedBack();
    }
  }

  Future<void> onGetFeedBack({bool reset = false}) async {
    // if (_isLoading || !_hasMore) return;
    // _isLoading = true;

    if (reset) {
      _page = 1;
      listFeedback.clear();
      _hasMore = true;
    }

    final response = await Repository.getFeedback(
      FeedbackFilterRequest(
        page: _page,
        pageSize: 20,
        productId: widget.productId,
        minRating: _selectedRating > 0 ? _selectedRating : null,
        maxRating: _selectedRating > 0 ? _selectedRating : null,
      ),
    );

    if (response.isEmpty) {
      // _hasMore = true;
    } else {
      listFeedback.addAll(response);
      _page++;
    }

    streamListFeedbak.add(listFeedback);
    setState(() {});
    _isLoading = false;
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      centerTitle: true,
      title: const Text(
        'Danh sách đánh giá',
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.w600,
          fontSize: 18,
        ),
      ),
      appBarLeading: Padding(
        padding: const EdgeInsets.only(top: 10),
        child: iconBack(context, color: Colors.white),
      ),
      isHide: false,
      hideBottomBarOnScroll: false,
      bodyBuilder: (_) {
        return Column(
          children: [
            SizedBox(
              height: 50,
              child: SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: List.generate(5, (index) {
                    final star = index + 1;
                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 4.0),
                      child: ChoiceChip(
                        color: MaterialStateProperty.all(
                          _selectedRating == star
                              ? toHexToColor(primaryButtonColor)
                              : Colors.grey[300],
                        ),
                        label: Text('$star ⭐'),
                        selected: _selectedRating == star,
                        onSelected: (_) {
                          setState(() => _selectedRating = star);
                          onGetFeedBack(reset: true);
                        },
                      ),
                    );
                  })..insert(
                    0,
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 4.0),
                      child: ChoiceChip(
                        color: MaterialStateProperty.all(
                          _selectedRating == 0
                              ? toHexToColor(primaryButtonColor)
                              : Colors.grey[300],
                        ),
                        label: const Text('Tất cả'),
                        selected: _selectedRating == 0,
                        onSelected: (_) {
                          setState(() => _selectedRating = 0);
                          onGetFeedBack(reset: true);
                        },
                      ),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 8),

            Padding(
              padding: const EdgeInsets.all(12.0),
              child: StreamBuilder<List<FeedbacFilterkResponse>>(
                stream: streamListFeedbak.stream,
                builder: (context, snapshot) {
                  final data = snapshot.data ?? [];

                  return MediaQuery.removePadding(
                    context: context,
                    removeTop: true,
                    child: ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      controller: _scrollController,
                      itemCount: data.length,
                      itemBuilder: (context, index) {
                        final feedback = data[index];
                        return Card(
                          child: Container(
                            decoration: BoxDecoration(
                              color: Colors.white70,
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: ListTile(
                              leading: CircleAvatar(
                                backgroundColor: Colors.grey[300],
                                child: SizedBox(
                                  width: 30,
                                  height: 30,
                                  child:
                                      (feedback.user?.image != null &&
                                              feedback.user!.image!.isNotEmpty)
                                          ? ClipOval(
                                            child: Image.asset(
                                              feedback.user!.image!,
                                              fit: BoxFit.cover,
                                            ),
                                          )
                                          : ClipOval(
                                            child: Image.asset(
                                              avtMen1,
                                              fit: BoxFit.cover,
                                            ),
                                          ),
                                ),
                              ),
                              title: Text(
                                feedback.user?.fullName ?? 'Người dùng',
                              ),
                              subtitle: Text(
                                feedback.comments ?? 'Không có nhận xét',
                              ),
                              trailing: Row(
                                mainAxisSize: MainAxisSize.min,
                                children: [
                                  Text(
                                    feedback.rating?.toString() ?? '0',
                                    style: const TextStyle(
                                      fontWeight: FontWeight.bold,
                                      fontSize: 16,
                                    ),
                                  ),
                                  const Icon(Icons.star, color: Colors.amber),
                                ],
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                  );
                },
              ),
            ),
          ],
        );
      },
    );
  }
}
