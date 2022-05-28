package com.example.myfavdishes.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myfavdishes.R
import com.example.myfavdishes.adapters.CustomListItemAdapter
import com.example.myfavdishes.databinding.ActivityAddUpdateDishBinding
import com.example.myfavdishes.databinding.DialogCustomImageSelectionBinding
import com.example.myfavdishes.databinding.DialogCustomListBinding
import com.example.myfavdishes.utils.Constants
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityAddUpdateDishBinding
    private var myImagePath = ""
    private lateinit var myCustomListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        binding.ivAddDishImage.setOnClickListener(this)

        /**ADD CLICK LISTENERS TO THE INPUT TEXT TO ACTIVATE THE CUSTOM DIALOG*/

        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.etCookingTime.setOnClickListener(this)
        binding.btnAddDish.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//implement the back button arrow
        binding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }
                R.id.et_type -> {
                    customItemsListDialog(resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE)
                    return
                }
                R.id.et_category -> {
                    customItemsListDialog(resources.getString(R.string.title_select_dish_category),
                        Constants.dishCategories(),
                        Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_cooking_time -> {
                    customItemsListDialog(resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.dishCookingTimes(),
                        Constants.DISH_COOKING_TIME)
                    return
                }
                R.id.btn_add_dish ->{
                    val title = binding.etTitle.text.toString().trim{it <= ' '} //this trim method will cut the empty spaces in the string i.e. "__stringtext stringtext_" it will cut the spaces where the underscore is
                    val type = binding.etType.text.toString().trim{it <= ' '}
                    val category = binding.etCategory.text.toString().trim{it <= ' '}
                    val ingredients = binding.etIngredients.text.toString().trim{it <= ' '}
                    val cookingTimeInMinutes = binding.etCookingTime.text.toString().trim{it <= ' '}
                    val cookingInstructions = binding.etDirectionToCook.text.toString().trim{it <= ' '}

                    when{
                        TextUtils.isEmpty(myImagePath) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_image),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(title) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_title),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(type) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_type),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(category) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_category),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(ingredients) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_ingredients),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingTimeInMinutes) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_cooking_time),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingInstructions) ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                                Toast.LENGTH_SHORT).show()
                        }
                        else ->{
                            Toast.makeText(this@AddUpdateDishActivity,
                            "All entries valid", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.DISH_TYPE -> {
                myCustomListDialog.dismiss()
                binding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                myCustomListDialog.dismiss()
                binding.etCategory.setText(item)
            }
            else -> {
                myCustomListDialog.dismiss()
                binding.etCookingTime.setText(item)
            }
        }
    }

    private fun customImageSelectionDialog() {
        val customImageDialog = Dialog(this)
        val binding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        customImageDialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this@AddUpdateDishActivity)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(cameraIntent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?,
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()
            customImageDialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddUpdateDishActivity, //we have to specify the activity context because we are inside the object scope
                        "You Have Denied  Permission To Access The Gallery", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    request: PermissionRequest?,
                    token: PermissionToken?,
                ) {
                    showRationalDialogForPermissions()

                }

            }).onSameThread().check()

            customImageDialog.dismiss()
        }

        customImageDialog.show()

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        //Setting the file
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        //creating the image
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    /**CREATE CUSTOM DIALOG TO DISPLAY LIST USING RECYCLER VIEW*/
    private fun customItemsListDialog(title: String, itemsList: List<String>, selection: String) {
         myCustomListDialog = Dialog(this)

        val binding = DialogCustomListBinding.inflate(layoutInflater)
        myCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)

        val adapter = CustomListItemAdapter(this, itemsList, selection)
        binding.rvList.adapter = adapter
        myCustomListDialog.show()

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras.let {
                    val thumbnail = data?.extras?.get("data") as Bitmap

                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(binding.ivDishImage)

                    myImagePath = saveImageToInternalStorage(bitmap = thumbnail)
                    Log.i("ImagePath ", myImagePath)
                    binding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_edit_pencil))
                }
            }

            if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data

                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .optionalCenterCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?, target: Target<Drawable>?, isFirstResource: Boolean,
                            ): Boolean {
                                Log.e("TAG", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                dataSource: DataSource?, isFirstResource: Boolean,
                            ): Boolean {
                                resource?.let {
                                    val bitmap = resource.toBitmap()
                                    myImagePath = saveImageToInternalStorage(bitmap)
                                    Log.i("ImagePath", myImagePath)
                                }
                                return false
                            }

                        })
                        .into(binding.ivDishImage)

                    // binding.ivDishImage.setImageURI(selectedPhotoUri)
                    binding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_edit_pencil))
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("cancelled", "Image Selection Cancelled by User")
            }
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off " +
                "permissions required for this feature. It can be enabled under  Application Settings")
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}
