<%@page import="com.amazonbird.db.data.Announcer"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	isELIgnored="true" session="false"%>

<div class="modal hide" id="addProductModal" tabindex="-1" role="dialog"
	aria-labelledby="Ürün Ekle" aria-hidden="true">

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">×</button>
		<h3 id="addProductModal">Ürün Ekle</h3>
	</div>

	<div data-role="modal-body">
		<div style="margin-left: 10px;">
			<form class="productImageUpload"
				action='<%=request.getContextPath()%>/rest/products' method="post"
				enctype="multipart/form-data">
				<fieldset>
					<div data-role="fieldcontain"
						class="ui-field-contain ui-body ui-br">
						<label for="message">Ürün Açıklaması</label>
						<textarea name="message" rows="8" style="width: 90%;"></textarea>
					</div>
					<div data-role="fieldcontain"
						class="ui-field-contain ui-body ui-br">
						<label for="price">Fiyat</label> <input type="text" name="price" />
					</div>
					<div data-role="fieldcontain"
						class="ui-field-contain ui-body ui-br">
						<label for="price">Anahtar Kelimeler</label> <input type="text" name="reasonvalue" />
					</div>
					<div data-role="fieldcontain">
						<label for="image">Fotoğraf</label> <input type="file"
							id="submitID" name="image" value="Upload" />
					</div>
					<div data-role="fieldcontain"
						class="ui-field-contain ui-body ui-br">
						<button class="btn btn-primary" type="submit" data-theme="a" class="ui-btn-hidden">Ürün Ekle</button>
					</div>
				</fieldset>
				<input type="hidden" name="announcerid" value="<%=request.getAttribute(Announcer.ANNOUNCER) == null ? "" : ((Announcer)request.getAttribute(Announcer.ANNOUNCER)).getId() %>">
			</form>
		</div>

	</div>
	<div class="modal-footer"></div>
</div>

