package com.influx.marcus.theatres.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import java.util.*

internal class FacebookHelper {
    private val permissions = Arrays.asList("public_profile ", "email", "user_birthday", "user_location")
    private var callbackManager: CallbackManager? = null
    private var loginManager: LoginManager? = null
    private var shareDialog: ShareDialog? = null
    private val activity: Activity?
    private val fragment: Fragment
    private var fbSignInListener: OnFbSignInListener? = null

    /**
     * Interface to listen the Facebook login
     */
    interface OnFbSignInListener {
        fun OnFbSignInComplete(graphResponse: GraphResponse?, error: String?)
    }

    constructor(activity: Activity, fbSignInListener: OnFbSignInListener, activity1: Activity?, fragment: Fragment) {
        this.activity = activity
        this.fbSignInListener = fbSignInListener
        this.fragment = fragment
    }

    constructor(fragment: Fragment, fbSignInListener: OnFbSignInListener, activity: Activity?, fragment1: Fragment) {
        this.fragment = fragment
        this.fbSignInListener = fbSignInListener
        this.activity = activity
    }

    constructor(activity: Activity, activity1: Activity?, fragment: Fragment) {
        shareDialog = ShareDialog(activity)
        this.activity = activity1
        this.fragment = fragment
    }

    constructor(fragment: Fragment, activity: Activity?, fragment1: Fragment) {
        shareDialog = ShareDialog(fragment)
        this.activity = activity
        this.fragment = fragment1
    }


    fun connect() {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
        if (activity != null)
            loginManager!!.logInWithReadPermissions(activity, permissions)
        else
            loginManager!!.logInWithReadPermissions(fragment, permissions)
        loginManager!!.registerCallback(callbackManager!!,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        if (loginResult != null) {
                            callGraphAPI(loginResult.accessToken)
                        }
                    }

                    override fun onCancel() {
                        fbSignInListener!!.OnFbSignInComplete(null, "User cancelled.")
                    }

                    override fun onError(exception: FacebookException) {
                        if (exception is FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut()
                            }
                        }
                        fbSignInListener!!.OnFbSignInComplete(null, exception.message)
                    }
                })

    }

    private fun callGraphAPI(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response -> fbSignInListener!!.OnFbSignInComplete(response, null) }
        val parameters = Bundle()
        //Explicitly we need to specify the fields to get values else some values will be null.
        parameters.putString("fields", "id,birthday,email,first_name,gender,last_name,link,location,name")
        request.parameters = parameters
        request.executeAsync()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (callbackManager != null)
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * To share the details in facebook wall.
     *
     * @param title       of the content
     * @param description of the content
     * @param url         link to share.
     */
    fun shareOnFBWall(title: String, description: String, url: String) {
        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent = ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse(url))
                    .build()
            shareDialog!!.show(linkContent)
        }
    }


}