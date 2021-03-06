package com.sun.hero_01.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import com.sun.hero_01.R
import com.sun.hero_01.base.BaseFragment
import com.sun.hero_01.data.model.Favourite
import com.sun.hero_01.data.model.HeroDetail
import com.sun.hero_01.data.model.HeroSpell
import com.sun.hero_01.data.source.FavouriteRepository
import com.sun.hero_01.data.source.HeroRepository
import com.sun.hero_01.data.source.local.FavouriteLocalDataSource
import com.sun.hero_01.data.source.remote.HeroRemoteDataSource
import com.sun.hero_01.ui.favorite.FavoriteFragment
import com.sun.hero_01.ui.skin.SkinFragment
import com.sun.hero_01.utils.Constant
import com.sun.hero_01.utils.ImageType
import com.sun.hero_01.utils.ToolbarIcon
import com.sun.hero_01.utils.extensions.loadHeroImage
import com.sun.hero_01.utils.extensions.replaceFragment
import com.sun.hero_01.utils.extensions.setHeroClassImage
import com.sun.hero_01.utils.extensions.showIcon
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment(), DetailContract.View {

    override val layoutResourceId = R.layout.fragment_detail
    override var bottomNavigationViewVisibility = View.GONE

    private var isFavouriteHero = false
    private var favourite: Favourite? = null
    private var idHero: String? = null
    private var detailPresenter: DetailPresenter? = null
    private val heroAbilityAdapter by lazy {
        HeroAbilityAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEvent()
    }

    private fun initView() {

        arguments?.let {
            idHero = it.getString(ARGUMENT_HERO_ID).toString()
        }

        detailPresenter = DetailPresenter(
            HeroRepository.getInstance(HeroRemoteDataSource.getInstance()),
            FavouriteRepository.getInstance(FavouriteLocalDataSource.getInstance(requireActivity()))
        )
        detailPresenter?.setView(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerHeroAbility.apply {
            setHasFixedSize(true)
            adapter = heroAbilityAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        detailPresenter?.let {
            idHero?.let { id -> it.getHeroDetail(id) }
        }
    }

    override fun onStop() {
        super.onStop()
        detailPresenter?.onStop()
    }
    
    override fun initToolbar() {
        this@DetailFragment.toolbar?.apply {
            title = idHero
            showIcon(ToolbarIcon.RETURN)
        }
    }

    private fun initEvent() {
        imageFavorite.setOnClickListener {
            favourite?.let {
                updateFavorite(it)
                FavoriteFragment.isCheckFavourite = !FavoriteFragment.isCheckFavourite
            }
        }
    }

    private fun updateFavorite(favorite: Favourite) {
        detailPresenter?.let {
            if (isFavouriteHero)
                it.deleteHero(favorite.heroId)
            else
                it.insertHero(favorite)
            isFavouriteHero = !isFavouriteHero
            selectedFavorite()
        }
    }

    override fun loadHeroDetailOnSuccess(heroDetail: HeroDetail) {
        heroDetail.run {
            favourite = Favourite(
                id.toString(),
                name.toString(),
                Constant.BASE_URL + "/"
                    + Constant.BASE_VERSION + "/"
                    + Constant.PATH_IMAGE_CHAMPION + "/"
                    + image.toString()
            )
        }
        applyDataToView(heroDetail)
    }

    private fun applyDataToView(heroDetail: HeroDetail) {
        heroDetail.apply {
            textName.text = name
            textTitle.text = title
            textPrimaryTag.text = primaryTag
            textSecondaryTag.text = secondaryTag
            this.info?.let {
                progressAttack.progress = it.attack ?: 0
                progressDefense.progress = it.defense ?: 0
                progressMagic.progress = it.magic ?: 0
                progressDifficult.progress = it.difficulty ?: 0
            }
            stats?.let {
                textHealNumber.text = it.hp.toString()
                textArmorNumber.text = it.armor.toString()
                textAttackNumber.text = it.attackDamage.toString()
                textMovementNumber.text = it.moveSpeed.toString()
                textRangeNumber.text = it.attackRange.toString()
                textAttackSpeedNumber.text = it.attackSpeed.toString()
                textHealRegenNumber.text = it.hpRegen.toString()
                textMagicResistNumber.text = it.spellBlock.toString()
            }
            imageHero.loadHeroImage(image, ImageType.SQUARE)
            imagePrimaryTag.setHeroClassImage(primaryTag)
            secondaryTag?.let {
                imageSecondaryTag.setHeroClassImage(it)
                imageSecondaryTag.visibility = View.VISIBLE
            }
            skins?.let {
                imageHeroBackground.loadHeroImage("${this.id}_${it[0].num}.jpg", ImageType.SKIN)
                imageDefaultSkin.loadHeroImage("${this.id}_${it[0].num}.jpg", ImageType.SKIN)
                imageSecondSkin.loadHeroImage("${this.id}_${it[1].num}.jpg", ImageType.SKIN)
            }
            heroAbilityAdapter.updateData(spells as MutableList<HeroSpell>?)
            textLoreDescription.text = lore
            isFavouriteHero = isFavorite
            selectedFavorite()
        }
        imageDefaultSkin.setOnClickListener {
            replaceFragment(SkinFragment.newInstance(idHero, heroDetail.skins), R.id.frameContainer)
        }
        imageSecondSkin.setOnClickListener {
            replaceFragment(SkinFragment.newInstance(idHero, heroDetail.skins), R.id.frameContainer)
        }
    }

    private fun selectedFavorite() {
        if (isFavouriteHero)
            imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
    }

    override fun onError(exception: Exception?) {
        exception?.let {
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARGUMENT_HERO_ID = "ARGUMENT_HERO_ID"

        fun newInstance(heroName: String?) = DetailFragment().apply {
            arguments = bundleOf(ARGUMENT_HERO_ID to heroName)
        }
    }
}
