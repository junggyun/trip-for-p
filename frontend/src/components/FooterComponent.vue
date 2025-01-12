<script setup>
// 외부 링크이므로 script setup은 비워둡니다
</script>

<template>
    <div class="footer-container">
        <div class="footer-content">
            <div class="footer-info">
                <p>트립 포 피 | P를 위한 여행 플래너</p>
                <div class="footer-links">
                    <a href="https://policies.google.com/terms?hl=ko"
                       target="_blank"
                       rel="noopener noreferrer"
                       class="footer-link">Google 이용약관</a>
                    <span class="divider">|</span>
                    <a href="https://policies.google.com/privacy?hl=ko"
                       target="_blank"
                       rel="noopener noreferrer"
                       class="footer-link">Google 개인정보처리방침</a>
                </div>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; {{ new Date().getFullYear() }} Trip For P. All rights reserved.</p>
        </div>
    </div>
</template>

<style scoped>
.footer-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: end;
    padding: 20px 0;
    background: linear-gradient(135deg, #5c6ac4 0%, #8794d8 100%);
    color: #FFFFFF;
}

.footer-content {
    width: 60%;
    max-width: 1200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.footer-info {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.footer-info p {
    margin: 5px 0;
    color: #FFFFFF;
    font-weight: 500;
}

.footer-links {
    display: flex;
    gap: 15px;
    align-items: center;
    margin: 10px 0;
}

.footer-link {
    color: #FFFFFF;
    text-decoration: none;
    font-size: 0.9em;
    padding: 5px 10px;
    border-radius: 8px;
    transition: all 0.3s ease;
    background-color: rgba(255, 255, 255, 0.2);
}

.footer-link:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
}

.divider {
    color: rgba(255, 255, 255, 0.5);
}

.footer-bottom {
    width: 100%;
    text-align: center;
    padding: 15px 0 5px 0;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    margin-top: 15px;
}

.footer-bottom p {
    font-size: 0.9em;
    color: rgba(255, 255, 255, 0.8);
    margin: 0;
}

@media (max-width: 1024px) {
    .footer-content {
        width: 80%;
    }
}

@media (max-width: 768px) {
    .footer-content {
        width: 90%;
        padding: 0 20px;
    }
}

@media (max-width: 480px) {
    .footer-links {
        flex-direction: column;
        gap: 10px;
    }

    .divider {
        display: none;
    }

    .footer-info p {
        font-size: 0.9em;
    }

    .footer-link {
        width: 100%;
        padding: 8px 0;
    }
}
</style>
