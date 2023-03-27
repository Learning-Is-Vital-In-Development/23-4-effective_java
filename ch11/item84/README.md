---
marp: true
backgroundColor: #FFFFF0
theme: gaia
_class: lead
---

## 프로그램의 동작을 쓰레드 스케줄러에 기대지 말라

### 최 혁

---

## 쓰레드 스케줄러

- 쓰레드 스케줄러는 여러 쓰레드가 실행중일 때 각 쓰레드의 실행 시점을 관리한다.
    - 어떤 쓰레드가 먼저 실행될지
    - 언제 쓰레드를 종료할지
    - 종료했을 때 쓰레드를 어디로 보낼지

- 각 운영체제마다 구체적인 스케줄링 정책이 다르다.

- 가능한 쓰레드의 평균적인 수를 프로세스 수보다 지나치게 많아지지 않도록 해야 한다.

---

## 쓰레드 수를 적게 유지하는 방법

- 각 쓰레드가 무언가 유용한 작업을 완료한 후에는 다음 일거리가 생기기 전까지 대기하는 것이다.
__쓰레드는 당장 처리해야 할 작업이 없다면 실행돼서는 안 된다.__

- 쓰레드는 busy waiting 상태가 되면 안 된다.
    - busy waiting: OS에서는 원하는 자원을 얻기 위해 기다리는 것이 아니라 권한을 얻을 때까지 계속 확인하는 것을 의미한다.
---

```java
public class SlowCountDownLatch {
	private int count;
	
	public SlowCountDownLatch(int count) {
		if (count < 0)
			throw new IllegalArgumentException(count + " < 0");
		this.count = count;
	 }
	
	public void await() { // busy waiting
		while (true) {
			synchronized(this) {
				if (count == 0)
					return;
	     }
	   }
	 }

	public synchronized void countDown() {
	if (count != 0)
	      count--;
	  }
}
```

---

## 주의사항

- Thread.yield를 사용하면 오히려 성능이 나빠질 수 있고, 테스트할 수단도 없다.
=> 차라리 애플리케이션 구조를 바꿔 동시 대기 상태의 스레드 수가 적어지도록 하자

- 되도록 쓰레드 우선순위를 임의로 조정하지 말자
    - 이미 잘 동작하는 프로그램의 서비스 품질을 높이기 위해선 사용 가능
    - 다만, 거의 동작하지 않는 프로그램을 고치기 위해 사용하지 말자
