package com.ai.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ai.entity.Location;
import com.ai.entity.Products;
import com.ai.exception.ApplicationException;
import com.ai.repository.LocationRepository;
import com.ai.repository.ProductsRepository;

@Service
public class ProductService {

	@Autowired
	private ProductsRepository productsRepository;

	@Autowired
	LocationRepository locationRepository;

	public List<Products> getAllFoodItems() {
		return productsRepository.findAll();
	}

	public String saveOrUpdateFoodItem(Products productsRequest, MultipartFile productImg)
			throws ApplicationException, IOException {
		
		Optional<Location> location = locationRepository.findById(productsRequest.getLocation().getLocationId());
		
		if (!location.isPresent()) {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Location not found");
		}
		
		if (productsRequest.getProductId() == null) {
			
			Products productExists = productsRepository.findByProductNameAndLocationLocationId(
					productsRequest.getProductName(), productsRequest.getLocation().getLocationId());
			
			if (productExists != null) {
				throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
						"Food Item already exists with same name");
			}
			
			Products products = new Products();
			BeanUtils.copyProperties(productsRequest, products);
			products.setProductPrice(Double.parseDouble(String.format("%.2f", productsRequest.getProductPrice())));
			products.setProductActive(true);
			products.setLocation(location.get());
			products.setUpdatedDate(LocalDateTime.now());

			if (productImg != null && !productImg.isEmpty()) {
				products.setProductImage(productImg.getBytes());
			}

			productsRepository.save(products);

			return "Food item saved";
			
		} else {
			Optional<Products> products = productsRepository.findById(productsRequest.getProductId());
			
			if (products.isPresent()) 
			{
				Products productExists = productsRepository.findByProductNameAndLocationLocationIdAndProductIdNotIn(
						productsRequest.getProductName(), productsRequest.getLocation().getLocationId(),
						Arrays.asList(products.get().getProductId()));
			
				
				
				if (productExists != null) {
					throw new ApplicationException(HttpStatus.CONFLICT, 1001, LocalDateTime.now(),
							"Food Item already exists with same name");
				}
				
				
				
				byte[] img = products.get().getProductImage();
				Products product = products.get();
				BeanUtils.copyProperties(productsRequest, product);
				product.setProductPrice(Double.parseDouble(String.format("%.2f", productsRequest.getProductPrice())));
				product.setProductActive(true);
				product.setLocation(location.get());
				product.setUpdatedDate(LocalDateTime.now());

				if (productImg != null && !productImg.isEmpty()) {
					product.setProductImage(productImg.getBytes());
				} else {
					product.setProductImage(img);
				}

				productsRepository.save(product);

				return "Food item updated";
			} else {
				throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No food item found");
			}
		}
	}

	public String deleteFoodItemById(Long id) throws ApplicationException {
		
		Optional<Products> product = productsRepository.findById(id);
		
		if (product.isPresent()) {
		
			productsRepository.delete(product.get());

				return "Food item removed";
			
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No food item found");
		}
	}

	public Products getFoodItemsById(Long id) {
		Optional<Products> product = productsRepository.findById(id);
		if (product.isPresent()) {
			return product.get();
		}
		return null;
	}


	public List<Products> getAllProductsByLocation(Long id) throws ApplicationException {
	//	Optional<Location> location = locationRepository.findById(id);
		
		List<Products> products = productsRepository.findByLocationLocationId(id);
		
		if (products!=null) {
			
			return products;
			
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "No products found in the location");
		}
	}

	public boolean validateImageFile(MultipartFile multipartFile) {
		BufferedImage image;
		try {
			image = ImageIO.read(multipartFile.getInputStream());
			if (image == null) {
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String activeOrInactiveProduct(Products products) throws ApplicationException {
		Optional<Products> product = productsRepository.findById(products.getProductId());
		if (product.isPresent()) {
			if (products.isProductActive() == true) {
				product.get().setProductActive(true);
				product.get().setUpdatedDate(LocalDateTime.now());
				product.get().setProductUpdatedBy(products.getProductUpdatedBy());
				productsRepository.save(product.get());

				return "Product is active";
			} else {
				product.get().setProductActive(false);
				product.get().setUpdatedDate(LocalDateTime.now());
				product.get().setProductUpdatedBy(products.getProductUpdatedBy());
				productsRepository.save(product.get());

				return "Product is inactive";
			}
		} else {
			throw new ApplicationException(HttpStatus.NOT_FOUND, 1001, LocalDateTime.now(), "Product not found");
		}
	}

	public List<Products> getActiveProducts(Long locationId) {
		return productsRepository.findByLocationLocationIdAndProductActive(locationId, true);
	}
}
