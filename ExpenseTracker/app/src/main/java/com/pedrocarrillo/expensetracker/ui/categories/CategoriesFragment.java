package com.pedrocarrillo.expensetracker.ui.categories;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.CategoriesAdapter;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class CategoriesFragment extends MainFragment implements TabLayout.OnTabSelectedListener {

    private @IExpensesType int mCurrentMode = IExpensesType.MODE_EXPENSES;

//    private List<String> tabList;
    private RecyclerView rvCategories;
    private TextView tvEmpty;

    private List<Category> mCategoryList;
    private CategoriesAdapter mCategoriesAdapter;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoryList = new ArrayList<>();
        mCategoriesAdapter = new CategoriesAdapter(mCategoryList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        rvCategories = (RecyclerView)rootView.findViewById(R.id.rv_categories);
        tvEmpty = (TextView)rootView.findViewById(R.id.tv_empty);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        tabList = Arrays.asList(getString(R.string.expenses), getString(R.string.income));
//        mMainActivityListener.setTabs(tabList, this);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        reloadData();
        mMainActivityListener.setFAB(R.drawable.ic_add_white_48dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().createEditTextDialog(getActivity(), getString(R.string.create_category), getString(R.string.save), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            EditText etTest = (EditText) ((AlertDialog) dialog).findViewById(R.id.et_main);
                            Category category = new Category(etTest.getText().toString(), mCurrentMode);
                            RealmManager.getInstance().save(category, Category.class);
                            reloadData();
                        }
                    }
                });
            }
        });
        mMainActivityListener.setTitle(getString(R.string.categories));

        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setAdapter(mCategoriesAdapter);
        rvCategories.setHasFixedSize(true);
        rvCategories.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_10dp)));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Category category = (Category) viewHolder.itemView.getTag();
                DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_delete).concat(category.getName()), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            RealmManager.getInstance().delete(category);
                        }
                        reloadData();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvCategories);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (tab.getTag()!=null) {
            if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.expenses))) {
                mCurrentMode = IExpensesType.MODE_EXPENSES;
            } else if (tab.getTag().toString().equalsIgnoreCase(getString(R.string.income))) {
                mCurrentMode = IExpensesType.MODE_INCOME;
            }
        }
        reloadData();
    }

    private void reloadData() {
        mCategoryList = Category.getCategoriesForType(mCurrentMode);
        if (mCategoryList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        if (mCategoriesAdapter == null) {
            mCategoriesAdapter = new CategoriesAdapter(mCategoryList);
        } else {
            mCategoriesAdapter.updateCategories(mCategoryList);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}