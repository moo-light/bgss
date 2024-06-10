import 'dart:math';

import 'package:blockchain_mobile/1_controllers/providers/live_rate_provider.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:syncfusion_flutter_charts/charts.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../../../models/liverates/time_frames.dart';

class PriceCharts extends StatefulWidget {
  const PriceCharts({super.key});

  @override
  State<PriceCharts> createState() => _PriceChartsState();
}

class _PriceChartsState extends State<PriceCharts> {
  List<TimeFrames> data = [];
  ChartType type = ChartType.W;
  @override
  void initState() {
    super.initState();
    Provider.of<LiveRateProvider>(context, listen: false)
        .getTypeFromSharedPreferences()
        .then((value) => setState(() {
              type = value;
            }));
  }

  @override
  Widget build(BuildContext context) {
    var provider = Provider.of<LiveRateProvider>(context);
    var width = MediaQuery.of(context).size.width;
    final isLoading = provider.isChartLoading;
    if (isLoading) {
      return Container(
          constraints: BoxConstraints(
            minHeight: width,
            minWidth: width,
          ),
          child: IsLoadingWG(isLoading: isLoading));
    }
    data = provider.rateHistory.map((e) => TimeFrames.fromLiveRate(e)).toList();
    var map = data.map((e) => (e.y ?? 0.0) as double);
    var sfCartesianChart = SfCartesianChart(
      plotAreaBorderWidth: 0,
      legend: const Legend(
          isVisible: true, overflowMode: LegendItemOverflowMode.wrap),
      primaryXAxis: DateTimeAxis(
        initialZoomPosition: 1,
        initialZoomFactor: 0.3,
        dateFormat: DateFormat.jms(),
        autoScrollingMode: AutoScrollingMode.end,
      ),
      onZoomReset: (ZoomPanArgs args) {
        print(args.currentZoomFactor);
        print(args.currentZoomPosition);
      },
      onZoomStart: (args) {
        print(args.currentZoomFactor);
      },
      primaryYAxis: NumericAxis(
        minimum: data.isEmpty ? 1000 : 0,
        maximum: data.isEmpty ? 4000 : map.reduce(max) + 1000,
        interactiveTooltip: const InteractiveTooltip(enable: true),
        labelFormat: '\${value}',
      ),
      tooltipBehavior: TooltipBehavior(enable: true),
      zoomPanBehavior: ZoomPanBehavior(
        enableDoubleTapZooming: true,
        enableMouseWheelZooming: true,
        enablePanning: true,
        zoomMode: ZoomMode.x,
        enablePinching: true,
        enableSelectionZooming: true,
      ),
      crosshairBehavior: CrosshairBehavior(
          activationMode: ActivationMode.singleTap,
          enable: true,
          lineType: CrosshairLineType.horizontal,
          shouldAlwaysShow: true),
      series: <CartesianSeries>[
        // Renders hiloOpenCloseSeries
        LineSeries<TimeFrames, DateTime>(
          dataSource: [...data],
          name: "USD/XAU",
          xValueMapper: (TimeFrames sales, _) => sales.x,
          yValueMapper: (TimeFrames sales, _) => sales.y,
          color: kSecondaryColor,
          emptyPointSettings:
              const EmptyPointSettings(mode: EmptyPointMode.drop),
          markerSettings:
              const MarkerSettings(isVisible: true, width: 3, height: 3),
        )
      ],
    );
    return Center(
      child: sfCartesianChart,
    );
  }
}

class TradingViewChart extends StatefulWidget {
  const TradingViewChart({super.key});

  @override
  TradingViewChartState createState() => TradingViewChartState();
}

class TradingViewChartState extends State<TradingViewChart> {
  final WebViewController _controller = WebViewController();
  final String chartData = '''
          <!-- TradingView Widget BEGIN -->
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=0.7">
          </head>
          <div class="tradingview-widget-container" style="height:100%;width:100%">
            <div class="tradingview-widget-container__widget" style="height:calc(100% - 32px);width:100%"></div>
            <script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-advanced-chart.js" async>
                {
                  "autosize": true,
                  "symbol": "FOREXCOM:XAUUSD",
                  "interval": "D",
                  "timezone": "Etc/UTC",
                  "theme": "light",
                  "style": "1",
                  "locale": "en",
                  "withdateranges": true,
                  "allow_symbol_change": false,
                  "calendar": false,
                  "show_popup_button": true,
                  "popup_width": "1000",
                  "popup_height": "650",
                  "support_host": "https://www.tradingview.com"
                }
            </script>
          </div>
          <!-- TradingView Widget END -->
          ''';
  @override
  Widget build(BuildContext context) {
    _controller
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(Colors.white)
      ..setNavigationDelegate(
        NavigationDelegate(
          onProgress: (int progress) {
            // Update loading bar.
          },
          onPageStarted: (String url) {},
          onPageFinished: (String url) {},
          onWebResourceError: (WebResourceError error) {},
          onNavigationRequest: (NavigationRequest request) {
            if (request.url.startsWith('https://www.youtube.com/')) {
              return NavigationDecision.prevent;
            }
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadHtmlString(chartData);
    final width = MediaQuery.of(context).size.width;
    return SizedBox(
      height: width, // Specify the height
      width: width, // Specify the height
      child: WebViewWidget(
        controller: _controller,
      ),
    );
  }
}
