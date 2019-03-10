static private boolean checkSpeed(Pair<Date, Pair<Double, Double>> a, Pair<Date, Pair<Double, Double>> b, long lim){
   double dist = 111.1111111 * ((a.second.first - b.second.first) * (a.second.first - b.second.first) +
        Math.cos(a.second.first) * ((a.second.second - b.second.second) * (a.second.second - b.second.second)));
   double time = TimeUnit.HOURS.convert(Math.abs(a.first.getTime() - b.first.getTime()), TimeUnit.MILLISECONDS);
   return Math.abs(a.first.getTime() - b.first.getTime()) > INTERVAL || dist < lim * lim * time * time
          || dist > 120 * 120 * time * time;
}
