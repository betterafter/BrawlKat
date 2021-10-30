package com.keykat.keykat.brawlkat.service.maprecommendservice.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.keykat.keykat.brawlkat.service.maprecommendservice.ui.EventAdapter.ViewHolder
import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendContract
import com.keykat.keykat.brawlkat.service.model.data.NotificationData
import com.keykat.keykat.brawlkat.common.util.KatData
import com.keykat.keykat.brawlkat.common.util.parser.kat_eventsParser.pair
import java.util.*
import com.keykat.keykat.brawlkat.R
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EventAdapter(
    private val context: Context,
    private var eventArrayList: ArrayList<pair>?,
    private var brawlersArrayList: ArrayList<HashMap<String, Any>>?
) : RecyclerView.Adapter<ViewHolder>(), MapRecommendContract.RecyclerView {

    private var playerBrawlersArrayList: ArrayList<String> = ArrayList()
    private var isUserRecommend = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.service_map_event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.onBind(position, eventArrayList, brawlersArrayList)
            holder.onBrawlerRecommendsBind(position, eventArrayList, brawlersArrayList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return eventArrayList!!.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateRecommendState(state: Boolean) {
        isUserRecommend = state
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun refresh(notificationData: NotificationData) {
        eventArrayList = notificationData.eventArrayList
        brawlersArrayList = notificationData.brawlerArrayList
        notifyDataSetChanged()
    }

    // 상단 이벤트 바인딩
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val backgroundImageView: ImageView = itemView.findViewById(R.id.item_background)
        private val modeTypeImageView: ImageView = itemView.findViewById(R.id.item_modeType)
        private val mapNameTimeTextView: TextView = itemView.findViewById(R.id.item_mapNameAndTime)
        private val mapTypeTextView: TextView = itemView.findViewById(R.id.item_mapType)
        private val recommendLayout: LinearLayout = itemView.findViewById(R.id.item_RecommendsLayout)
        private var options: RequestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .priority(Priority.HIGH)
            .format(DecodeFormat.PREFER_RGB_565)

        private val metrics = context.resources.displayMetrics
        val height = Math.min(metrics.heightPixels, metrics.widthPixels)
        val width = Math.max(metrics.heightPixels, metrics.widthPixels)

        private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 기본 이미지 세팅
        fun onBind(
            position: Int,
            eventArrayList: ArrayList<pair>?,
            brawlersArrayList: ArrayList<HashMap<String, Any>>?
        ) {

            eventArrayList?.let {
                val backgroundUrl = it[position].info["mapTypeImageUrl"] as String?
                val gameModeTypeUrl = it[position].info["gameModeTypeImageUrl"] as String?
                val mapName = it[position].info["mapName"] as String?
                val name = it[position].info["name"] as String?

                mapNameTimeTextView.text = mapName
                mapTypeTextView.text = name

                Glide.with(context)
                    .load(backgroundUrl)
                    .override(width / 2, height / 5)
                    .into(backgroundImageView)

                Glide.with(context)
                    .load(gameModeTypeUrl)
                    .override(width / 3 / 10, width / 3 / 10)
                    .into(modeTypeImageView)
            }
        }

        // 전체 브롤러 추천 뷰
        @SuppressLint("SetTextI18n")
        fun onBrawlerRecommendsBind(
            position: Int,
            eventArrayList: ArrayList<pair>?,
            brawlersArrayList: ArrayList<HashMap<String, Any>>?
        ) {
            recommendLayout.removeAllViews()

            if (isUserRecommend) {
                if (KatData.eventsPlayerData.value != null) {
                    val data = KatData.eventsPlayerData.value!!.brawlerData
                    for (i in data.indices) {
                        playerBrawlersArrayList.add(data[i].name)
                    }
                }
            }


            eventArrayList?.let {
                it[position].wins?.let { win ->

                    var verticalLayout = LinearLayout(context)
                    win.forEach { winData ->
                        val brawlerId = winData["brawler"]
                        brawlersArrayList?.let { list ->
                            // 브롤러 리스트를 다 돌면서
                            list.forEach { brawlerData ->
                                // 각 브를러 데이터에 "id"가 있을 때
                                brawlerData["id"]?.let { id ->
                                    // 승률 좋은 순서의 브롤러와 전체 브롤러의 id 비교
                                    if (id.toString() == brawlerId.toString()) {
                                        // 유저가 가진 브롤러와 전체 브롤러는 다르다
                                        if (isUserRecommend) {
                                            brawlerData["name"]?.let { name ->
                                                if (name.toString()
                                                        .uppercase(Locale.getDefault()) in playerBrawlersArrayList
                                                ) {
                                                    val brawlerView = setBrawlerView(brawlerData, winData)
                                                    verticalLayout.addView(brawlerView)
                                                }
                                            }
                                        } else {
                                            val brawlerView = setBrawlerView(brawlerData, winData)
                                            verticalLayout.addView(brawlerView)
                                        }
                                        if (verticalLayout.childCount == 5) {
                                            recommendLayout.addView(verticalLayout)
                                            verticalLayout = LinearLayout(context)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (verticalLayout.childCount != 5) {
                        recommendLayout.addView(verticalLayout)
                    }
                } ?: run {
                    val loadingText = TextView(context)
                    loadingText.text = context.getString(R.string.overdrawErrorMessage)

                    // ResourcesCompat.getFont 를 이용하여 폰트 불러오기.
                    val face = ResourcesCompat.getFont(context, R.font.lilita_one)
                    loadingText.typeface = face
                    loadingText.textSize = 20f
                    loadingText.setTextColor(Color.BLACK)
                    recommendLayout.addView(loadingText)
                }
            } ?: run {

            }

        }

        @SuppressLint("SetTextI18n")
        private fun setBrawlerView(
            brawlerData: HashMap<String, Any>,
            winData: HashMap<String, Any>
        ): View {
            val brawlerView = layoutInflater.inflate(R.layout.service_map_event_item_brawler, null)
            val brawlerImage =
                brawlerView.findViewById<ImageView>(R.id.map_event_item_brawler_img)
            val brawlerName =
                brawlerView.findViewById<TextView>(R.id.map_event_item_brawler_name)
            val brawlerWinRate =
                brawlerView.findViewById<TextView>(R.id.map_event_item_brawler_winRate)
            val brawlersImageUrl = brawlerData["imageUrl"] as String?

            brawlersImageUrl?.let {
                Glide.with(context)
                    .applyDefaultRequestOptions(options)
                    .load(brawlersImageUrl)
                    .override(width / 3 / 7, width / 3 / 7)
                    .into(brawlerImage)
            }

            brawlerName.text = brawlerData["name"].toString()

            var winRate = winData["winRate"].toString().toDouble()
            winRate = Math.round(winRate * 100) / 100.0

            if (winRate * 100 % 10 == 0.0) {
                brawlerWinRate.text = "" + winRate + "0%"
            } else {
                brawlerWinRate.text = "$winRate%"
            }
            return brawlerView
        }
    }
}