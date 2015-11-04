package com.JTechMod.custom.jrecyclerview;

import com.JTechMod.R;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 自定义recyclerview
 * 
 * @author wuxubaiyang
 *
 */
@SuppressWarnings("rawtypes")
public class JRecyclerView extends RecyclerView {
	/**
	 * 加载状态-正常状态
	 */
	private static final int LOAD_STATE_NORMAL = 0x9527;
	/**
	 * 加载状态-加载中
	 */
	private static final int LOAD_STATE_LOADING = 0x9528;
	/**
	 * 加载状态-加载失败
	 */
	private static final int LOAD_STATE_FAIL = 0x9529;
	/**
	 * 加载状态-无更多数据
	 */
	private static final int LOAD_STATE_NOMORE = 0x9530;
	/**
	 * 加载状态标志位
	 */
	private int loadState = LOAD_STATE_NORMAL;
	/**
	 * layoutmanager状态-线性布局
	 */
	private static final int LAYOUT_STATE_LINEAR = 0x9531;
	/**
	 * layoutmanager状态-表格布局
	 */
	private static final int LAYOUT_STATE_GRID = 0x9532;
	/**
	 * layoutmanager状态-瀑布流布局
	 */
	private static final int LAYOUT_STATE_STAGGERED = 0x9533;
	/**
	 * layoutmanager标志位
	 */
	private int layout_state = LAYOUT_STATE_LINEAR;
	/**
	 * 是否向下滚动
	 */
	private boolean isScrollDown = false;
	/**
	 * 加载监听
	 */
	private OnLoadListener onLoadListener;
	/**
	 * item点击事件
	 */
	private OnItemClickListener onItemClickListener;
	/**
	 * 外部包裹的适配器
	 */
	private mAdapter mAdapter;

	public JRecyclerView(Context context) {
		this(context, null);
	}

	public JRecyclerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public JRecyclerView(Context context, AttributeSet attrs, int arg2) {
		super(context, attrs, arg2);
	}

	/**
	 * 设置加载更多监听-必须在setadapter之前调用
	 * 
	 * @param onLoadListener
	 *            加载更多监听
	 */
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.onLoadListener = onLoadListener;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param onItemClickListener
	 *            点击事件回调
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * 设置状态为正在加载
	 */
	public void setLoading() {
		this.loadState = LOAD_STATE_LOADING;
		mAdapter.modifyState();
	}

	/**
	 * 设置状态为加载失败
	 */
	public void setLoadFail() {
		this.loadState = LOAD_STATE_FAIL;
		mAdapter.modifyState();
	}

	/**
	 * 设置状态为无更多数据
	 */
	public void setLoadFinish() {
		this.loadState = LOAD_STATE_NOMORE;
		mAdapter.modifyState();
	}

	/**
	 * 设置状态为加载完成
	 */
	public void setLoadComplete() {
		this.loadState = LOAD_STATE_NORMAL;
		mAdapter.modifyState();
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = new mAdapter(adapter);
		super.setAdapter(mAdapter);
	}

	@Override
	public void setLayoutManager(LayoutManager layout) {
		super.setLayoutManager(layout);
		if (layout instanceof GridLayoutManager) {
			layout_state = LAYOUT_STATE_GRID;
		} else if (layout instanceof LinearLayoutManager) {
			layout_state = LAYOUT_STATE_LINEAR;
		} else if (layout instanceof StaggeredGridLayoutManager) {
			layout_state = LAYOUT_STATE_STAGGERED;
		}
	}

	@Override
	public void onScrolled(int dx, int dy) {
		super.onScrolled(dx, dy);
		this.isScrollDown = dy > 0;
	}

	@Override
	public void onScrollStateChanged(int state) {
		super.onScrollStateChanged(state);
		if (RecyclerView.SCROLL_STATE_IDLE == state && isScrollDown && null != onLoadListener
				&& loadState == LOAD_STATE_NORMAL) {
			boolean flag = true;
			if (layout_state == LAYOUT_STATE_LINEAR) {// 线性布局
				int lastPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
				flag = lastPosition == (mAdapter.getItemCount() - 1);
			} else if (layout_state == LAYOUT_STATE_GRID) {// 表格布局
				int lastPosition = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
				flag = lastPosition == (mAdapter.getItemCount() - 1);
			} else if (layout_state == LAYOUT_STATE_STAGGERED) {// 交错布局
				int[] lastPositions = ((StaggeredGridLayoutManager) getLayoutManager())
						.findLastVisibleItemPositions(null);
				int footer = mAdapter.getItemCount() - 1;
				for (int i = 0; i < lastPositions.length; i++) {
					if (lastPositions[i] != footer) {
						flag = false;
						break;
					}
				}
			}
			loadState = flag ? LOAD_STATE_LOADING : LOAD_STATE_NORMAL;
			if (flag) {// 如果标志状态为加载中，则回调方法
				onLoadListener.loadMore();
				mAdapter.modifyState();
			}
		}
	}

	/**
	 * 自定义适配器，包裹用户设置的适配器，实现添加足部（加载更多）功能
	 * 
	 * @author JTech
	 *
	 */
	private class mAdapter extends Adapter {
		private static final int ITEM_FOOTER = 0x9527;
		private RecyclerHolder recyclerHolder;
		private Adapter adapter;

		public mAdapter(Adapter adapter) {
			this.adapter = adapter;
		}

		@Override
		public int getItemCount() {
			if (null != onLoadListener) {
				return adapter.getItemCount() + 1;
			}
			return adapter.getItemCount();
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onBindViewHolder(ViewHolder holder, final int position) {
			if (getItemViewType(position) == ITEM_FOOTER) {
				if (layout_state == LAYOUT_STATE_STAGGERED) {
					StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView
							.getLayoutParams();
					layoutParams.setFullSpan(true);
					holder.itemView.setLayoutParams(layoutParams);
				} else if (layout_state == LAYOUT_STATE_GRID) {
					final GridLayoutManager gridLayoutManager = ((GridLayoutManager) getLayoutManager());
					final SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
					gridLayoutManager.setSpanSizeLookup(new SpanSizeLookup() {
						@Override
						public int getSpanSize(int position) {
							if (null != onLoadListener && position == mAdapter.getItemCount() - 1) {
								return gridLayoutManager.getSpanCount();
							} else if (null != spanSizeLookup) {
								return spanSizeLookup.getSpanSize(position);
							}
							return 1;
						}
					});
				}
				return;
			}
			adapter.onBindViewHolder(holder, position);
			holder.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != onItemClickListener) {
						onItemClickListener.onItemClick(null, v, position, 0);
					}
				}
			});
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			if (viewType == ITEM_FOOTER) {
				LayoutInflater layoutInflater = LayoutInflater.from(getContext());
				View view = layoutInflater.inflate(R.layout.load_footer, parent, false);
				recyclerHolder = new RecyclerHolder(view, getContext());
				return recyclerHolder;
			}
			return adapter.onCreateViewHolder(parent, viewType);
		}

		@Override
		public int getItemViewType(int position) {
			if (null != onLoadListener && position == getItemCount() - 1) {
				return ITEM_FOOTER;
			}
			return adapter.getItemViewType(position);
		}

		/**
		 * 修改足部状态
		 */
		public void modifyState() {
			if (null != recyclerHolder) {
				switch (loadState) {
				case LOAD_STATE_FAIL:// 加载失败
					recyclerHolder.setText(R.id.textview_load_footer, "加载失败，请点击重试");
					recyclerHolder.getView(R.id.progressbar_load_footer).setVisibility(View.GONE);
					recyclerHolder.itemView.setClickable(true);
					recyclerHolder.itemView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (loadState == LOAD_STATE_FAIL) {
								onLoadListener.retryClick();
								loadState = LOAD_STATE_LOADING;
								modifyState();
							}
						}
					});
					break;
				case LOAD_STATE_LOADING:// 加载中
					recyclerHolder.setText(R.id.textview_load_footer, "正在加载中");
					recyclerHolder.getView(R.id.progressbar_load_footer).setVisibility(View.VISIBLE);
					break;
				case LOAD_STATE_NOMORE:// 无更多数据
					recyclerHolder.setText(R.id.textview_load_footer, "已经到底了");
					recyclerHolder.getView(R.id.progressbar_load_footer).setVisibility(View.GONE);
					break;
				case LOAD_STATE_NORMAL:// 正常状态
					recyclerHolder.setText(R.id.textview_load_footer, "滑动加载更多");
					recyclerHolder.getView(R.id.progressbar_load_footer).setVisibility(View.GONE);
					break;
				}
			}
		}
	}

	/**
	 * 加载更多回调
	 * 
	 * @author wuxubaiyang
	 *
	 */
	public static interface OnLoadListener {
		/**
		 * 加载更多
		 */
		void loadMore();

		/**
		 * 点击重试
		 */
		void retryClick();
	}
}