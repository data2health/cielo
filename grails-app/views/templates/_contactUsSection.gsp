<section class="mbr-section form1 cid-qIfhXo2QYg">
    <div id="contact-us-form" class="container">
        <div class="row justify-content-center">
            <div class="title col-12 col-lg-8">
                <h2 class="mbr-section-title align-center pb-3 mbr-fonts-style display-2">
                    CONTACT US</h2>
                <h3 class="mbr-section-subtitle align-center mbr-light pb-3 mbr-fonts-style display-5">Comments or questions? Leave us a message.
                </h3>
            </div>
        </div>
    </div>
    <div class="container">
        <div class="row justify-content-center">
            <div class="media-container-column col-lg-8">
                <form class="mbr-form" action="${createLink(controller: "communications", action: "contactUs")}" method="post" data-form-title="Contact Us Form">

                    <sec:ifNotLoggedIn>
                        <div class="row row-sm-offset">
                            <div class="col-md-4 multi-horizontal" data-for="name">
                                <div class="form-group">
                                    <label class="form-control-label mbr-fonts-style display-7" for="name-form1-f">Name</label>
                                    <input type="text" class="form-control" name="name" data-form-field="Name" required="" id="name-form1-f">
                                </div>
                            </div>
                            <div class="col-md-4 multi-horizontal" data-for="email">
                                <div class="form-group">
                                    <label class="form-control-label mbr-fonts-style display-7" for="email-form1-f">Email</label>
                                    <input type="email" class="form-control" name="email" data-form-field="Email" required="" id="email-form1-f">
                                </div>
                            </div>
                            <div class="col-md-4 multi-horizontal" data-for="phone">
                                <div class="form-group">
                                    <label class="form-control-label mbr-fonts-style display-7" for="phone-form1-f">Phone</label>
                                    <input type="tel" class="form-control" name="phone" data-form-field="Phone" id="phone-form1-f">
                                </div>
                            </div>
                        </div>
                    </sec:ifNotLoggedIn>

                    <div class="form-group">
                        <label class="form-control-label mbr-fonts-style display-7" for="name-form1-f">Subject</label>
                        <input type="text" class="form-control" name="subject" data-form-field="Subject" required="" id="subject-form1-f">
                    </div>
                    <div class="form-group" data-for="message">
                        <label class="form-control-label mbr-fonts-style display-7" for="message-form1-f">Message</label>
                        <textarea type="text" class="form-control" name="message" rows="7" data-form-field="Message" id="message-form1-f"></textarea>
                    </div>

                    <span class="input-group-btn"><button href="" type="submit" class="btn btn-primary btn-form display-4">Submit</button></span>
                </form>
            </div>
        </div>
    </div>
</section>