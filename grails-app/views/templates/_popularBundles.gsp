<section class="testimonials1 cid-qHGbIv2b05" id="testimonials1-d">
    <div class="container">
        <div class="media-container-row">
            <div class="title col-12 align-center">
                <h2 class="pb-3 mbr-fonts-style display-2">
                    Popular Public Bundles</h2>
                <h3 class="mbr-section-subtitle mbr-light pb-3 mbr-fonts-style display-5"></h3>
            </div>
        </div>
    </div>

    <div class="container pt-3 mt-2">
        <div id="publicBundles" class="media-container-row">
            %{--Iterate through and populate the most popular bundles--}%
            <g:each in="${bundles}" var="bundle">
                <div id="${bundle.projectId}" class="mbr-testimonial p-3 align-center col-12 col-md-6 col-lg-4">
                    <div class="panel-item p-3">
                        <div class="card-block">
                            <div style="display: inline-block; overflow: hidden">
                                <g:getUserProfilePic user="${bundle.projectOwnerUserObject}" sticker="${true}" imageSize="xxx-large"/>
                            </div>
                            <p class="mbr-text mbr-fonts-style display-7">
                                ${bundle.description}
                            </p>
                        </div> 
                        <div class="card-footer"> 
                            <div class="mbr-author-name mbr-bold mbr-fonts-style display-7">
                                ${bundle.projectOwner}
                            </div>
                            <small class="mbr-author-desc mbr-italic mbr-light mbr-fonts-style display-7">
                                ${bundle.ownerInstitution}
                            </small>
                            </div>
                        </div> 
                </div>
            </g:each>
        </div>
    </div>
</section>