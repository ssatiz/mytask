package com.influx.marcus.theatres.api


import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardReq
import com.influx.marcus.theatres.api.ApiModels.DeleteCard.DeleteCardResp
import com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard.DeleteSavedCardReq
import com.influx.marcus.theatres.api.ApiModels.DeleteSavedCard.DeleteSavedCardResp
import com.influx.marcus.theatres.api.ApiModels.HomeMoviePosters.HomeMoviePosterResp
import com.influx.marcus.theatres.api.ApiModels.NearCinemas.NearCinemasRequest
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresGPS.OTGPSResp
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSReq
import com.influx.marcus.theatres.api.ApiModels.OurTheatresNonGPS.OTNonGPSResp
import com.influx.marcus.theatres.api.ApiModels.Status
import com.influx.marcus.theatres.api.ApiModels.UserInfoReq
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatReq
import com.influx.marcus.theatres.api.ApiModels.blockseat.BlockSeatResp
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationReq
import com.influx.marcus.theatres.api.ApiModels.bookingconfirmation.BookingConfirmationResp
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookReq
import com.influx.marcus.theatres.api.ApiModels.cancelbooking.CancelBookingResp
import com.influx.marcus.theatres.api.ApiModels.carddetail.CardDetailReq
import com.influx.marcus.theatres.api.ApiModels.carddetail.CardDetailResp
import com.influx.marcus.theatres.api.ApiModels.enrollcard.EnrollCardReq
import com.influx.marcus.theatres.api.ApiModels.enrollcard.EnrollCardResp
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardReq
import com.influx.marcus.theatres.api.ApiModels.enrollgiftcard.EnrollGiftCardResp
import com.influx.marcus.theatres.api.ApiModels.filter.FilterReq
import com.influx.marcus.theatres.api.ApiModels.filter.FilterResp
import com.influx.marcus.theatres.api.ApiModels.filter.FilteredMoviesReq
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordReq
import com.influx.marcus.theatres.api.ApiModels.forgotpassword.ForgotPasswordResp
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceReq
import com.influx.marcus.theatres.api.ApiModels.getgiftcardbalance.GiftCardBalanceResp
import com.influx.marcus.theatres.api.ApiModels.getpref.PrefDataReq
import com.influx.marcus.theatres.api.ApiModels.giftdelete.GiftDeleteReq
import com.influx.marcus.theatres.api.ApiModels.giftdelete.GiftDeleteResp
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegReq
import com.influx.marcus.theatres.api.ApiModels.guest.GuestRegResp
import com.influx.marcus.theatres.api.ApiModels.home.HomeRequest
import com.influx.marcus.theatres.api.ApiModels.home.HomeResponse
import com.influx.marcus.theatres.api.ApiModels.liststatecity.cityListResp
import com.influx.marcus.theatres.api.ApiModels.liststatecity.pincodeListResp
import com.influx.marcus.theatres.api.ApiModels.liststatecity.statelistResp
import com.influx.marcus.theatres.api.ApiModels.login.FacebookRequest
import com.influx.marcus.theatres.api.ApiModels.login.GoogleRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginRequest
import com.influx.marcus.theatres.api.ApiModels.login.LoginResponse
import com.influx.marcus.theatres.api.ApiModels.myaccount.MyAccountReq
import com.influx.marcus.theatres.api.ApiModels.myaccount.MyAccountResp
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateAccountReq
import com.influx.marcus.theatres.api.ApiModels.myaccountupdate.UpdateResponse
import com.influx.marcus.theatres.api.ApiModels.payment.PaymentStatusReq
import com.influx.marcus.theatres.api.ApiModels.paymentcardlist.PaymentCardListReq
import com.influx.marcus.theatres.api.ApiModels.paymentcardlist.PaymentCardListResp
import com.influx.marcus.theatres.api.ApiModels.preferedlocations.PreferedLocsResp
import com.influx.marcus.theatres.api.ApiModels.rewardcarddetail.RewardCardDetailReq
import com.influx.marcus.theatres.api.ApiModels.rewardcarddetail.RewardCardDetailResp
import com.influx.marcus.theatres.api.ApiModels.rewardlogin.RewardLoginRequest
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardCardsListReq
import com.influx.marcus.theatres.api.ApiModels.rewardscardslist.RewardsCardsListResp
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardTransactionReq
import com.influx.marcus.theatres.api.ApiModels.rewardtransaction.RewardTransactionResp
import com.influx.marcus.theatres.api.ApiModels.savedcards.SavedCardReq
import com.influx.marcus.theatres.api.ApiModels.savedcards.SavedCardResp
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatReq
import com.influx.marcus.theatres.api.ApiModels.seatlayout.SeatResp
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.showtime.ShowtimeResponse
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDReq
import com.influx.marcus.theatres.api.ApiModels.showtimemoviedetail.ShowtimeMDResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RTomatoResp
import com.influx.marcus.theatres.api.ApiModels.showtimerottentomato.RtomatoReq
import com.influx.marcus.theatres.api.ApiModels.signup.SignupReq
import com.influx.marcus.theatres.api.ApiModels.signup.SignupResp
import com.influx.marcus.theatres.api.ApiModels.specials.SpecialResp
import com.influx.marcus.theatres.api.ApiModels.statelist.StateListResp
import com.influx.marcus.theatres.api.ApiModels.theatre.TheatreListReq
import com.influx.marcus.theatres.api.ApiModels.theatre.TheatreListResp
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeRequest
import com.influx.marcus.theatres.api.ApiModels.theatreshowtime.TheatreShowtimeResp
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.LockUnreservedResp
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedReq
import com.influx.marcus.theatres.api.ApiModels.unreservedbooking.UnreservedResp
import com.influx.marcus.theatres.api.ApiModels.updatePref.UpdatePrefReq
import com.influx.marcus.theatres.api.ApiModels.updatePref.resp.UpdatePrefResp
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyReq
import com.influx.marcus.theatres.api.ApiModels.upgradeloyalty.UpgradeLoyaltyResp
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionReq
import com.influx.marcus.theatres.api.ApiModels.versioncheck.VersionResp
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Kavitha on 02-04-2018.
 *
 */
interface ApiInterface {

    @POST("GetUserInfo")
    fun getUserinfo(@Body userInfoReq: UserInfoReq): Call<Status>

    @POST("GetPaymentStatus")
    fun getPaymentStatus(@Body paymentreq: PaymentStatusReq): Call<Status>

    @Headers("Content-Type: application/json")
    @POST("application/versionCheck")
    fun checkVersionDetails(@Header("Authorization") authorization: String,
                            @Body versioncheck: VersionReq): Call<VersionResp>

    @POST("locations/getPreferenceData")
    fun getPreferenceData(@Header("Authorization") authorization: String,
                          @Body req: PrefDataReq): Call<PreferedLocsResp>


    @GET("locations/theatresByState/{state}")
    fun getPreferedLocationsBasedOnState(@Header("Authorization") authorization: String,
                                         @Path("state") stateCode: String,
                                         @Query("lat") latitude: String,
                                         @Query("long") longitude: String): Call<PreferedLocsResp>

    @GET("locations/theatresByState/{state}")
    fun getLocationsBasedOnState(@Header("Authorization") authorization: String,
                                 @Path("state") stateCode: String): Call<PreferedLocsResp>

    @GET("locations")
    fun getAllLocations(@Header("Authorization") authorization: String): Call<StateListResp>

    @POST("cinema/getSchedulesByPreferences")
    fun getSchedulesByPreferences(@Header("Authorization") authorization: String,
                                  @Body homeRequest: HomeRequest): Call<HomeResponse>


    @POST("cinema/getScheduledMovies")
    fun getHomeScreenMoviesdata(@Header("Authorization") authorization: String,
                                @Body homeRequest: HomeRequest): Call<HomeMoviePosterResp>

    @POST("cinema/getShowtimes")
    fun getShowtimes(@Header("Authorization") authorization: String,
                     @Body showtimeRequest: ShowtimeRequest): Call<ShowtimeResponse>

    @POST("cinema/getMovieDetails")
    fun getMovieDetails(@Header("Authorization") authorization: String,
                        @Body movieDetailsReq: ShowtimeMDReq): Call<ShowtimeMDResp>

    @POST("cinema/getRatingsByMoviename")
    fun getRTScore(@Header("Authorization") authorization: String,
                   @Body req: RtomatoReq): Call<RTomatoResp>


    @POST("user/guestRegistration")
    fun registerGuestUser(@Header("Authorization") authorization: String,
                          @Body req: GuestRegReq): Call<GuestRegResp>

    @POST("layout")
    fun getSeatLayout(@Header("Authorization") authorization: String,
                      @Body req: SeatReq): Call<SeatResp>

    @POST("layout/lockSeats")
    fun blockSeat(@Header("Authorization") authorization: String,
                  @Body req: BlockSeatReq): Call<BlockSeatResp>

    @POST("layout/lockUnreservedTickets")
    fun lockUnreservedTickets(@Header("Authorization") authorization: String,
                              @Body req: LockUnreservedReq): Call<LockUnreservedResp>

    @POST("user/login")
    fun getLoginResp(@Header("Authorization") authorization: String,
                     @Body req: LoginRequest): Call<LoginResponse>

    @POST("user/googleLogin")
    fun googleLogin(@Header("Authorization") authorization: String,
                    @Body req: GoogleRequest): Call<LoginResponse>

    @POST("user/facebookLogin")
    fun facebookLogin(@Header("Authorization") authorization: String,
                      @Body req: FacebookRequest): Call<LoginResponse>

    @POST("user/registration")
    fun registration(@Header("Authorization") authorization: String,
                     @Body req: SignupReq): Call<SignupResp>


    @POST("user/forgetPassword")
    fun getForgotpassword(@Header("Authorization") authorization: String,
                          @Body req: ForgotPasswordReq): Call<ForgotPasswordResp>

    @POST("sales/confirmation")
    fun getBookingReviewConf(@Header("Authorization") authorization: String,
                             @Body req: BookingConfirmationReq): Call<BookingConfirmationResp>

    @POST("user/rewardLogin")
    fun getRewardLogin(@Header("Authorization") authorization: String,
                       @Body req: RewardLoginRequest): Call<LoginResponse>

    @POST("user/myAccount")
    fun getMyAccount(@Header("Authorization") authorization: String,
                     @Body req: MyAccountReq): Call<MyAccountResp>

    @POST("user/updateAccount")
    fun getUpdateAccount(@Header("Authorization") authorization: String,
                         @Body req: UpdateAccountReq): Call<UpdateResponse>


    @POST("rewards/enrollCard")
    fun getEnrollCardResponse(@Header("Authorization") authorization: String,
                              @Body req: EnrollCardReq): Call<EnrollCardResp>

    @POST("gifts/enrollCard")
    fun getEnrollGiftCardResponse(@Header("Authorization") authorization: String,
                                  @Body req: EnrollGiftCardReq): Call<EnrollGiftCardResp>


    @POST("rewards/getRewardTransactions")
    fun getRewardTransactions(@Header("Authorization") authorization: String,
                              @Body req: RewardTransactionReq): Call<RewardTransactionResp>

    @POST("rewards/getRewardCardDetails")
    fun getRewardCardDetails(@Header("Authorization") authorization: String,
                             @Body req: RewardCardDetailReq): Call<RewardCardDetailResp>

    @POST("user/rewardCardslist")
    fun getRewardCardslist(@Header("Authorization") authorization: String,
                           @Body req: RewardCardsListReq): Call<RewardsCardsListResp>

    @GET("cities/allstates")
    fun getStates(@Header("Authorization") authorization: String): Call<statelistResp>

    @GET("cities/{state}")
    fun getCity(@Header("Authorization") authorization: String,
                @Query("state") stateCode: String): Call<cityListResp>

    @GET("cities/{city}")
    fun getPincode(@Header("Authorization") authorization: String,
                   @Query("city") cityCode: String): Call<pincodeListResp>

    @POST("layout/getTicketTypes")
    fun getUnreservedTicketType(@Header("Authorization") authorization: String,
                                @Body req: UnreservedReq): Call<UnreservedResp>

    @POST("sales/paymentLists")
    fun paymentLists(@Header("Authorization") authorization: String,
                     @Body req: PaymentCardListReq): Call<PaymentCardListResp>

    @POST("sales/cancelSale")
    fun CancelBooking(@Header("Authorization") authorization: String,
                      @Body req: CancelBookReq): Call<CancelBookingResp>

    @POST("user/removeLoyaltyCard")
    fun removeLoyaltyCard(@Header("Authorization") authorization: String,
                          @Body req: DeleteCardReq): Call<DeleteCardResp>

    @POST("rewards/getRewardsBalances")
    fun cardDetail(@Header("Authorization") authorization: String,
                   @Body req: CardDetailReq): Call<CardDetailResp>

    @GET("application/specialOffers")
    fun getspecialOffers(@Header("Authorization") authorization: String): Call<SpecialResp>

    @POST("cinema/getNearestCinemasShowtime")
    fun getNearestCinemasShowtime(@Header("Authorization") authorization: String,
                                  @Body req: NearCinemasRequest): Call<ShowtimeResponse>


    @POST("locations/theatresList")
    fun getTheatreList(@Header("Authorization") authorization: String, @Body req: TheatreListReq): Call<TheatreListResp>

    @POST("cinema/getTheatreDetails")
    fun getTheatreDetails(@Header("Authorization") authorization: String, @Body req: TheatreShowtimeRequest): Call<TheatreShowtimeResp>

    @POST("user/savedCards")
    fun getSavedCards(@Header("Authorization") authorization: String, @Body req: SavedCardReq): Call<SavedCardResp>

    @POST("cinema/getMovieFilters")
    fun getMovieFilters(@Header("Authorization") authorization: String, @Body req: FilterReq): Call<FilterResp>

    @POST("cinema/getMoviesByFilter")
    fun getMoviesByFilter(@Header("Authorization") authorization: String, @Body req: FilteredMoviesReq): Call<HomeResponse>


    @POST("user/updateuserPreference")
    fun updateUsersPreferences(@Header("Authorization") authorization: String,
                               @Body req: UpdatePrefReq): Call<UpdatePrefResp>

    @POST("gifts/getBalance")
    fun getBalanceGiftCardResponse(@Header("Authorization") authorization: String,
                                   @Body req: GiftCardBalanceReq): Call<GiftCardBalanceResp>


    @POST("gifts/removeGiftCard")
    fun removeThisGiftCardByUserID(@Header("Authorization") authorization: String,
                                   @Body req: GiftDeleteReq): Call<GiftDeleteResp>


    @POST("user/savedCardDelete")
    fun deleteThisSavedCard(@Header("Authorization") authorization: String,
                            @Body req: DeleteSavedCardReq): Call<DeleteSavedCardResp>


    @POST("locations/theatresListGps")
    fun getOurTheatresGPS(@Header("Authorization") authorization: String,
                          @Body req: OTGPSReq): Call<OTGPSResp>

    @POST("locations/theatresListNonGps")
    fun getOurTheatresNonGPS(@Header("Authorization") authorization: String,
                             @Body req: OTNonGPSReq): Call<OTNonGPSResp>

    @POST("user/upgradeMemberToLoyalty")
    fun upgradeMemberToLoyalty(@Header("Authorization") authorization: String,
                               @Body req: UpgradeLoyaltyReq): Call<UpgradeLoyaltyResp>

}