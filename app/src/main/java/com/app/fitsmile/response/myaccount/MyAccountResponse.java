package com.app.fitsmile.response.myaccount;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MyAccountResponse{

	@SerializedName("location_datas")
	private List<LocationDatasItem> locationDatas;

	@SerializedName("user_datas")
	private UserDatas userDatas;

	@SerializedName("fav_datas")
	private FavDatas favDatas;

	@SerializedName("status")
	private String status;

	public List<LocationDatasItem> getLocationDatas(){
		return locationDatas;
	}

	public UserDatas getUserDatas(){
		return userDatas;
	}

	public FavDatas getFavDatas(){
		return favDatas;
	}

	public String getStatus(){
		return status;
	}
}